/**
 * YearMonth Search — inverse search DTOs.
 *
 * 現有 [destiny.core.astrology.prediction] 全部是 evaluation(給時間 → 算影響)。
 * 本檔補上 inverse search(給目標條件 → 找出符合的年/月),回答任何「何時」問題。
 *
 * 設計見 docs/timing-search-design.md。引擎保持「吉凶中性」:只輸出強度 + 原始特徵,
 * 好壞判斷與排序交給上層(LLM / 占星師)。
 *
 * 核心本體論 —— 兩種「打中主題」的 hit,差別只在時間長度:
 *  - [InstantHit]:在某一**刻**打中(`source: EventSource`,有 convergentTime)→ 機率式 OR 疊加。
 *  - [PeriodHit] :在一整**段**打中(`source: PeriodSource`,有 [from,to])→ 當乘數放大。
 */
package destiny.core.astrology.prediction

import destiny.core.RequestDto
import destiny.core.astrology.Aspect
import destiny.core.astrology.Arabic
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.AstrologyTraversalConfig
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.Stationary
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.astrology.eclipse.LunarType
import destiny.core.astrology.eclipse.SolarType
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IZodiacDegreeSerializer
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.ScoreTwoDecimalSerializer
import destiny.tools.serializers.YearMonthSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.YearMonth

/**
 * 段層([PeriodHit])的來源。
 *
 * 與 [EventSource] 本質不同:[EventSource] 的每個值都對應 `ITimeLineEvent` 的一個**時刻**
 * (`convergentTime`,相位精確的那一刻);而 [PeriodSource] 的每個值都是一**段期間**
 * (`[fromTime, toTime]`),在整段期間內持續生效。
 */
enum class PeriodSource {
  PROFECTION,           // 年小限:每年推進的「被啟動宮位」(整個 profection 年生效)
  MONTHLY_PROFECTION,   // 月小限:年內逐月推進的宮位(僅 grain==MONTH 才算;與年小限正交,可疊乘)
  ZODIACAL_RELEASING,   // 黃道釋放:L1~L4 各為一段
  FIRDARIA,             // 法達:major / minor period
  SOLAR_RETURN,         // 太陽回歸:回歸盤統管整年(houseOverlay)
  LUNAR_RETURN,         // 月亮回歸:回歸盤統管整月
}

/** 多 significator 共振要求。 */
enum class Combine {
  AND,  // 同一桶內需 ≥2 個不同 significator/宮同時啟動 → confluence 加成
  OR,   // 任一 significator 命中即收錄
}

/** 搜尋粒度。封頂 MONTH;DAY/HOUR 屬擇日,交棒給既有 [destiny.core.electional.DayHourService]。 */
enum class SearchGrain {
  YEAR,
  MONTH,
}

/**
 * funCall / service 進入點的查詢參數。
 *
 * 注意:`bdnp` **不在本 DTO 內**。沿用 [HoroscopeRangeAiTools] 慣例 —— 生辰由 app/session 脈絡
 * 提供,不由 LLM 經 JSON 傳入。service 簽章為 `search(bdnp, request)`。
 */
@RequestDto
@Serializable
data class YearMonthSearchRequest(
  /** LLM/使用者依語境給,e.g. now */
  @Serializable(with = LocalDateSerializer::class) val from: LocalDate,
  /** LLM/使用者依語境給(樂透~3個月;結婚~10年) */
  @Serializable(with = LocalDateSerializer::class) val to: LocalDate,
  /** LLM 從 Natal 解出的主題星體(e.g. 感情 → ASC ruler / 7th ruler / Venus) */
  val significators: List<AstroPoint>,
  /** 目標宮位 e.g. [5, 7];引擎會解出其宮主當作 target */
  val targetHouses: List<Int> = emptyList(),
  /** 目標阿拉伯點 e.g. [Arabic.Eros] */
  val targetLots: List<Arabic> = emptyList(),
  val combine: Combine = Combine.OR,
  val grain: SearchGrain = SearchGrain.MONTH,
)

/** 一個 hit 命中的目標(吉凶中性)。 */
@Serializable
sealed class HitTarget {
  /** 打中一顆 significator 本命星。 */
  @Serializable
  data class Significator(val point: AstroPoint) : HitTarget()

  /** 打中某目標宮的宮主。 */
  @Serializable
  data class House(val house: Int, val ruler: AstroPoint) : HitTarget()

  /** 打中某阿拉伯點。 */
  @Serializable
  data class Lot(val lot: Arabic) : HitTarget()
}

/**
 * 相位接觸幾何(命中是「經由相位」時才有)。`orb` 是度數(非 0..1),用 DoubleTwoDecimal。
 * 交給上層判好壞,引擎不判。
 */
@Serializable
data class AspectContact(
  val aspect: Aspect,
  @Serializable(with = DoubleTwoDecimalSerializer::class) val orb: Double,
  val applying: Boolean = false,
)

/**
 * 點層 hit:在某一**刻**打中主題(中性)。只給強度與原始特徵,好壞交給上層判。
 *
 * **兩條觸發通道**(見 docs/yearmonth-signal-recall.md §5.0a):
 *  - **相位通道**:某天體對 target 成相位 → `contact != null`。
 *  - **落宮通道**:事件(如滯留)落在某本命宮 → `contact == null`,target 為 [HitTarget.House]。
 *
 * 事件**種類**做成型別層級(與 [AstroEvent] 對稱),讓 [EclipseHit]/[StationHit] 帶各自的 rich 資料;
 * `source`([EventSource])保持「透過哪個推運法觀測」,不被食/滯留污染。
 */
@Serializable
sealed class InstantHit {
  abstract val source: EventSource
  abstract val target: HitTarget
  /** 負責的天體:行運星 / 食的發光體 / 滯留星 —— 一定有。 */
  abstract val transiting: AstroPoint
  /** 相位接觸;**null = 經宮位落點觸發(無相位)**。 */
  abstract val contact: AspectContact?
  /** 中性量級 ∈ [0,1](per-hit salience,聚合用)。離開 [0,1] 的是 [YearMonthWindow.strength]。 */
  abstract val rawStrength: Score

  /** 一般推運點對本命星成相位。 */
  @Serializable
  @SerialName("astroPoint")
  data class AstroPointHit(
    override val source: EventSource,
    override val target: HitTarget,
    override val transiting: AstroPoint,
    override val contact: AspectContact,
    @Serializable(with = ScoreTwoDecimalSerializer::class) override val rawStrength: Score,
    /** 行運星在該位置的必然尊貴。**不進量級**,純當特徵交給上層。 */
    val dignityScore: Int? = null,
  ) : InstantHit()

  /** 日月食對本命的接觸。`eclipse` 帶 SOLAR/LUNAR · TOTAL/PARTIAL/ANNULAR · magnitude 等 rich 資料。 */
  @Serializable
  @SerialName("eclipse")
  data class EclipseHit(
    override val source: EventSource,
    override val target: HitTarget,
    override val transiting: AstroPoint,
    override val contact: AspectContact?,
    @Serializable(with = ScoreTwoDecimalSerializer::class) override val rawStrength: Score,
    val eclipse: IEclipse,
  ) : InstantHit()

  /** 行星滯留(最強訊號)。`stationary` 帶順逆轉向,`zodiacDegree` 帶度數。 */
  @Serializable
  @SerialName("station")
  data class StationHit(
    override val source: EventSource,
    override val target: HitTarget,
    override val transiting: AstroPoint,
    override val contact: AspectContact?,
    @Serializable(with = ScoreTwoDecimalSerializer::class) override val rawStrength: Score,
    val stationary: Stationary,
    @Serializable(with = IZodiacDegreeSerializer::class) val zodiacDegree: IZodiacDegree,
  ) : InstantHit()

  /**
   * 換座 / 換宮(ingress):推運星進入新星座或新本命宮。**無相位 → `contact` 一律 null**(落點通道)。
   *  - [IngressKind.HOUSE]:`astroPoint` 進入 target house([newHouse] ∈ request.targetHouses)→ target 為 [HitTarget.House]。
   *  - [IngressKind.SIGN] :`astroPoint`(本身是 significator)換座 → target 為 [HitTarget.Significator]。
   *
   * cadence(見 docs §7):transit ingress(外行星)月級、solar-arc ingress 年級;
   * 強度依 source 縮放(SA > SECONDARY > TRANSIT),故 SA 換宮這種年級深刻標記自然較重。
   */
  @Serializable
  @SerialName("ingress")
  data class IngressHit(
    override val source: EventSource,
    override val target: HitTarget,
    override val transiting: AstroPoint,
    override val contact: AspectContact?,
    @Serializable(with = ScoreTwoDecimalSerializer::class) override val rawStrength: Score,
    val kind: IngressKind,
    /** 換座:舊→新星座(換宮時為 null)。 */
    val oldSign: ZodiacSign? = null,
    val newSign: ZodiacSign? = null,
    /** 換宮:舊→新本命宮(換座時為 null)。 */
    val oldHouse: Int? = null,
    val newHouse: Int? = null,
  ) : InstantHit()
}

/** ingress 種類:換座 vs 換宮。 */
enum class IngressKind { SIGN, HOUSE }

/**
 * 段層 hit:在一整**段**期間打中主題(profection / ZR / firdaria / return)。
 * 在聚合裡作為乘數放大同段內的 [InstantHit](即設計文件所謂「閘門 gate」)。
 */
@Serializable
data class PeriodHit(
  val source: PeriodSource,
  /** e.g. "profected 7th house", "year-lord Venus is significator", "ZR-Eros peak (L2)" */
  val reason: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class) val multiplier: Double,
)

/**
 * 一段時間窗的中性結果。
 *
 * `strength` 為**排序用的相對量級**(≥0):桶內以機率式 OR 聚合 [InstantHit](飽和 ≤1),再乘上
 * 各 [PeriodHit] 的 multiplier —— 故有段層命中時可能 >1,不額外 clamp,以保留排序解析度。
 * 好壞與最終排序由上層決定。
 *
 * (設計文件原寫 `period: ClosedRange<YearMonth>`,此處改用 `from`/`to` 兩欄以利序列化。)
 */
@Serializable
data class YearMonthWindow(
  @Serializable(with = YearMonthSerializer::class) val from: YearMonth,
  @Serializable(with = YearMonthSerializer::class) val to: YearMonth,
  @Serializable(with = DoubleTwoDecimalSerializer::class) val strength: Double,
  /** 點層貢獻者(top 數個)。 */
  val instantHits: List<InstantHit>,
  /** 段層貢獻者(被點亮的期間)。 */
  val periodHits: List<PeriodHit>,
)

/**
 * 全部權重抽出,不寫死。**這些沒有公認真值,是本系統最大的難點,刻意做成可調參**
 * (後續用 CelebrityService backtest 校準)。
 */
data class YearMonthScoringConfig(
  /** 推運法權重:SA 深遠/慢,Transit 快/表層。 */
  val sourceWeights: Map<EventSource, Double> = mapOf(
    EventSource.SOLAR_ARC to 1.0,
    EventSource.SECONDARY to 0.8,
    EventSource.TRANSIT to 0.7,
    EventSource.TERTIARY to 0.4,
    EventSource.MINOR to 0.3,
  ),
  /** 既有 [Aspect.Importance]:HIGH > MEDIUM > LOW。 */
  val importanceWeights: Map<Aspect.Importance, Double> = mapOf(
    Aspect.Importance.HIGH to 1.0,
    Aspect.Importance.MEDIUM to 0.6,
    Aspect.Importance.LOW to 0.3,
  ),
  /** 各相位專屬 maxOrb(用於 orbFactor 正規化)。 */
  val maxOrbs: Map<Aspect, Double> = mapOf(
    Aspect.CONJUNCTION to 8.0,
    Aspect.OPPOSITION to 8.0,
    Aspect.TRINE to 7.0,
    Aspect.SQUARE to 7.0,
    Aspect.SEXTILE to 5.0,
  ),
  val defaultMaxOrb: Double = 3.0,
  val applyingFactor: Double = 1.0,
  val separatingFactor: Double = 0.6,
  /** 各段層 [PeriodHit] 的乘數(落在啟動期間內的 [InstantHit] 才乘)。 */
  val periodMultipliers: Map<PeriodSource, Double> = mapOf(
    PeriodSource.PROFECTION to 1.5,
    PeriodSource.MONTHLY_PROFECTION to 1.3,   // 年內逐月細化;與年小限(不同源)疊乘讓目標月浮出同年其他月
    PeriodSource.ZODIACAL_RELEASING to 1.4,
    PeriodSource.FIRDARIA to 1.3,
    PeriodSource.SOLAR_RETURN to 1.3,
    PeriodSource.LUNAR_RETURN to 1.15,
  ),
  /** profection year-lord ∈ significators 時的額外乘數。 */
  val profectionLordMultiplier: Double = 1.3,
  /** AND/confluence:同桶 ≥2 個不同 significator 同時啟動時的加成。 */
  val confluenceBonus: Double = 1.25,
  /**
   * 段層乘數的**總上限**(防 background 蓋過 foreground)。
   * 同一 [PeriodSource] 內只取最強的一個乘數(去掉同技法重複計數,如 profected-house × year-lord),
   * 跨源相乘後再 coerce 至此上限。避免「小限主題年 / 滯留季」靠段層疊乘把實際事件月擠下榜。
   */
  val maxPeriodMultiplier: Double = 2.0,
  /**
   * **落宮通道**(無相位 orb)的固定強度 ∈ [0,1]。滯留 = 最強訊號 → 此處給高值。
   * 注意:這是 **salience(多響)**,非 valence(好壞);好壞仍由上層判。
   */
  val stationInHouseStrength: Score = 0.9.toScore(),
  /**
   * 日食「型別」的中性響度因子(salience,非 valence):全食/環食影響最深、偏食次之、半影最淺。
   * 乘進 [InstantHit.EclipseHit] 相位通道的 rawStrength(乘數 ≤1,故 Score 仍 ∈ [0,1]);查無 → 1.0。
   * 這是 salience 先驗(如同 [sourceWeights] 對推運法的先驗),**不判好壞**——全食 ≠ 凶,只是「更響」。
   */
  val solarEclipseFactor: Map<SolarType, Double> = mapOf(
    SolarType.TOTAL to 1.0,
    SolarType.HYBRID to 1.0,
    SolarType.ANNULAR to 0.9,
    SolarType.PARTIAL to 0.7,
  ),
  /** 月食「型別」的中性響度因子:全食 > 偏食 > 半影。語意同 [solarEclipseFactor]。 */
  val lunarEclipseFactor: Map<LunarType, Double> = mapOf(
    LunarType.TOTAL to 1.0,
    LunarType.PARTIAL to 0.7,
    LunarType.PENUMBRA to 0.4,
  ),
  /**
   * 換宮 ingress(推運星進入 target house)的中性基礎強度 ∈ [0,1]。
   * 實際 rawStrength = 此值 × `sourceWeights[source]`(故 SA 換宮 > transit 換宮,對齊 cadence)。
   * ingress = 真實但短暫的「宮位啟動」,弱於滯留落宮([stationInHouseStrength])。salience,非 valence。
   */
  val houseIngressStrength: Score = 0.6.toScore(),
  /** significator 換座 ingress 的中性基礎強度 ∈ [0,1];實際值同樣 × `sourceWeights[source]`。 */
  val signIngressStrength: Score = 0.5.toScore(),
  /**
   * 各相位的「強度偏好」權重 —— human lens 的第三個 dial(除 significators / targetHouses 外)。
   *
   * **引擎保持中性,不預設硬軟偏好**:預設空 map → 查無一律當 1.0(全相位等權)。
   * 由 client 依搜尋意圖填(僅作用於**相位通道**,不影響滯留落宮的 [stationInHouseStrength]):
   *  - 危機/事件類(血光、車禍、被害):偏硬相位(square/opp/conj)→ 見 [AspectWeights.HARD]
   *  - 和諧/流暢類(romantic love、貴人):偏軟相位(trine/sextile)→ 見 [AspectWeights.SOFT]
   *
   * 為何中性:硬相位「更激烈/更易出事」是 intensity,不是 valence;但「危機要硬、戀愛要軟」
   * 本身就是**搜尋意圖**而非普世真理,故做成可調 dial 而非寫死(避免戀愛搜尋永遠輸給刑沖)。
   */
  val aspectWeights: Map<Aspect, Double> = emptyMap(),
)

/**
 * 常見搜尋意圖的相位權重預設(human lens 的一部分;引擎本身中性)。
 * 未來併入完整 intent profile(significators + targetHouses + aspectWeights),可置於 companion 或 DB。
 */
object AspectWeights {
  /** 危機/事件類(血光、車禍、被害…):動態硬相位高、流暢軟相位低。 */
  val HARD: Map<Aspect, Double> = mapOf(
    Aspect.CONJUNCTION to 1.0, Aspect.OPPOSITION to 1.0, Aspect.SQUARE to 1.0,
    Aspect.TRINE to 0.5, Aspect.SEXTILE to 0.5,
  )

  /** 和諧/流暢類(romantic love、貴人運…):軟相位與合相高、刑沖低。 */
  val SOFT: Map<Aspect, Double> = mapOf(
    Aspect.CONJUNCTION to 1.0, Aspect.TRINE to 1.0, Aspect.SEXTILE to 1.0,
    Aspect.SQUARE to 0.5, Aspect.OPPOSITION to 0.5,
  )
}

/**
 * 搜尋的**運算/覆蓋控制**(與「問什麼」的 [YearMonthSearchRequest] 分離;有預設,通常由 app 設定)。
 *
 * 精神同 [AstrologyTraversalConfig] —— 一袋可調旋鈕,用來**關掉昂貴運算**(尤其長時間窗的月亮回歸)。
 * 設計重點:用「集合成員資格」同時 gate 掉「**運算**」與「**評分**」,昂貴的東西才真的被跳過,
 * 而非算了又不用。
 */
data class YearMonthSearchConfig(
  /** 點層:要撈哪些推運事件(對應 `getTimeLineEvents` 的 eventSourceConfigs)。移除某項 = 省掉該 traversal。 */
  val eventSourceConfigs: Set<EventSourceConfig> = setOf(
    EventSourceConfig(EventSource.TRANSIT, 0, 0),
    EventSourceConfig(EventSource.SOLAR_ARC, 0, 0),
    EventSourceConfig(EventSource.SECONDARY, 180, 2),
  ),
  /**
   * 段層:要評估哪些期間閘門。**成員資格也 gate 掉昂貴運算**:
   *  - [PeriodSource.LUNAR_RETURN] ∉ set → `withLunarReturns=false`(最耗時,長窗時尤甚)
   *  - [PeriodSource.ZODIACAL_RELEASING] ∉ set → 連 per-lot 的 `getRangeZodiacalReleasing` 都不跑
   */
  val periodSources: Set<PeriodSource> = PeriodSource.entries.toSet(),
  /** 回傳前 N 名 window。 */
  val topN: Int = 12,
  /** ZR 釋放的最大層數(L1..Ln)。 */
  val zrMaxLevel: Int = 2,
  /**
   * 底層 transit/aspect 計算設定(預設僅外行星行運)。
   * A1:在 [AstrologyTraversalConfig.YEARLY_FORECAST] 上開啟 `signIngress` / `houseIngress`,
   * 讓「換座 / 換宮」訊號浮現。因 transitingPlanets 僅外行星(無月亮/內行星),ingress 稀疏不洪泛;
   * scorer 端再以 significator / targetHouse 成員資格收斂。
   */
  val traversalConfig: AstrologyTraversalConfig = AstrologyTraversalConfig.YEARLY_FORECAST.copy(
    signIngress = true,
    houseIngress = true,
  ),
  /** 計分權重(可校準)。收進此 config → `search` 可逐次帶不同權重,直接支援 backtest 校準。 */
  val scoring: YearMonthScoringConfig = YearMonthScoringConfig(),
) {
  /** 月亮回歸最耗時,僅在 [PeriodSource.LUNAR_RETURN] 被啟用時才計算。 */
  val withLunarReturns: Boolean get() = PeriodSource.LUNAR_RETURN in periodSources

  /** 該段層是否要(計算並)評估。 */
  fun evaluates(source: PeriodSource): Boolean = source in periodSources
}
