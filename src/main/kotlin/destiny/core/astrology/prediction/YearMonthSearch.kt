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
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.YearMonthSerializer
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
  PROFECTION,           // 小限:每年(或每月)推進的「被啟動宮位」
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
 * 點層 hit:在某一**刻**打中主題(中性)。只給強度與原始特徵,好壞交給上層判。
 * `aspect` / `dignityScore` / `applying` 等原始特徵原樣呈現,引擎不替它們下吉凶結論。
 */
@Serializable
data class InstantHit(
  val source: EventSource,
  val transiting: AstroPoint,
  val target: HitTarget,
  /** 相位(調和/刑剋)—— 交給上層判好壞,引擎不判。非相位事件為 null。 */
  val aspect: Aspect? = null,
  val orb: Double = 0.0,
  val applying: Boolean = false,
  /** 行運星在該位置的必然尊貴。**不進量級**,純當特徵交給上層。 */
  val dignityScore: Int? = null,
  /** 0..1 中性量級。 */
  val rawStrength: Double,
)

/**
 * 段層 hit:在一整**段**期間打中主題(profection / ZR / firdaria / return)。
 * 在聚合裡作為乘數放大同段內的 [InstantHit](即設計文件所謂「閘門 gate」)。
 */
@Serializable
data class PeriodHit(
  val source: PeriodSource,
  /** e.g. "profected 7th house", "year-lord Venus is significator", "ZR-Eros peak (L2)" */
  val reason: String,
  val multiplier: Double,
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
  val strength: Double,
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
    PeriodSource.ZODIACAL_RELEASING to 1.4,
    PeriodSource.FIRDARIA to 1.3,
    PeriodSource.SOLAR_RETURN to 1.3,
    PeriodSource.LUNAR_RETURN to 1.15,
  ),
  /** profection year-lord ∈ significators 時的額外乘數。 */
  val profectionLordMultiplier: Double = 1.3,
  /** AND/confluence:同桶 ≥2 個不同 significator 同時啟動時的加成。 */
  val confluenceBonus: Double = 1.25,
)

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
  /** 底層 transit/aspect 計算設定(預設僅外行星行運)。 */
  val traversalConfig: AstrologyTraversalConfig = AstrologyTraversalConfig.YEARLY_FORECAST,
  /** 計分權重(可校準)。收進此 config → `search` 可逐次帶不同權重,直接支援 backtest 校準。 */
  val scoring: YearMonthScoringConfig = YearMonthScoringConfig(),
) {
  /** 月亮回歸最耗時,僅在 [PeriodSource.LUNAR_RETURN] 被啟用時才計算。 */
  val withLunarReturns: Boolean get() = PeriodSource.LUNAR_RETURN in periodSources

  /** 該段層是否要(計算並)評估。 */
  fun evaluates(source: PeriodSource): Boolean = source in periodSources
}
