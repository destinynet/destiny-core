/**
 * Created by smallufo on 2026-08-13.
 *
 * 占星圓盤的**呈現層 DTO** —— 專供 client 端 renderer 使用，與 [NatalV2] 刻意分離。
 *
 * 存在的理由與 [destiny.core.chinese.ziwei.ZiweiPlateDto] 相同：
 *
 * 1. **去識別化的型別保證**：[NatalV2] 帶有 `name` / `time` / `utc` / `location` / `place` / `age`。
 *    本 DTO **結構上就沒有宣告這些欄位**，因此「盤面可公開、當事人身分不外流」由編譯器把關，
 *    不倚賴輸出白名單的維護紀律。需要顯示身分時，由呼叫端另外組一個並列的身分物件。
 * 2. **座標攤平**：圓盤只需要「絕對黃經」。[NatalV2] 的位置藏在 `IZodiacDegree` 裡，
 *    且星體以 [AstroPoint] 當 map key（序列化為 nameKey），client 拿不到字形與分類。
 *    mapper 明確把 `zDeg`、`glyph`、`kind` 帶出來，renderer 才不必自維護對照表。
 *
 * 識別欄位一律用型別（[AstroPoint]、[Aspect]、[ZodiacSign]…），JSON 照樣是 nameKey 或 enum 名稱 —— 網頁端與凍結的 JSON 不受影響
 * （core-impl `HoroscopeChartDtoJsonCompatTest` 守著）。已翻譯的顯示文字另放 `label` 結尾的欄位，符號放 `glyph`。
 * ⚠️ [AstroPoint] 與 [ZodiacSign] 的 `toString()` 是中文名稱：要字串 id 一律寫 `.nameKey`／`.name`，不要靠插值。
 *
 * ⚠️ 命名規則：本檔案內**不得出現名為 `name` / `time` / `place` / `location` 的欄位**，
 * 人可讀字串一律叫 `label`。這讓「DTO 不含身分欄位」可以用欄位名精確比對來自動驗證（見對應測試）。
 *
 * 另外刻意**不含** `mundanePositions`（alt/az/時角可反推出生時間與地點）。
 */
package destiny.core.astrology

import destiny.core.ChartDensity
import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.Graph
import destiny.core.getAbbreviation
import destiny.tools.CircleTools.aheadOf
import destiny.tools.Lang
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import destiny.core.astrology.classical.Dignity

/** 顯示文字（`label` 結尾的欄位、[HoroscopePointDto.abbr]）的預設語系；呼叫端可另給 */
private val ZH = Lang.ZH_TW

@Serializable
data class HoroscopePointDto(
  /** 點位；JSON 是 [AstroPoint.nameKey]（如 `Planet.SUN`） */
  val point: AstroPoint,
  /** 顯示名（依 [toHoroscopeChartDto] 的 `lang`，預設繁中） */
  val label: String,
  /** unicode 字形（如 ☉），無則 null —— renderer 退回顯示 [label] */
  val glyph: String?,
  /** **絕對黃經 0–360**，圓盤唯一需要的座標 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val zDeg: Double,
  /** 星座；JSON 是名稱（如 `ARIES`） */
  val sign: ZodiacSign,
  /** 星座內度數 0–30 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val signDegree: Double,
  /** 宮位 1–12，無法判定為 null */
  val house: Int?,
  val retrograde: Boolean,
  /** 分類（[PointKind]）：renderer 以它分層 */
  val kind: PointKind,
  /** 縮寫（與 [label] 同語系：繁中如 日、月、昇，英文如 Su、Mo、As）—— 小空間的標籤用它，[label] 是全名 */
  val abbr: String = "",
  /** 英文縮寫（如 Su、Mo、AC；取自英文 bundle） */
  val abbrEn: String = "",
  /** 赤緯（度）；算不出時為 null */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val declination: Double? = null,
  /** 逆行階段（停滯前、逆行中、停滯後）；順行為 null。比 [retrograde] 細 */
  val retrogradePhase: RetrogradePhase? = null,
  /** 黃經速度（度／日，逆行為負）；軸點與虛點為 null */
  val speed: Double? = null,
  /**
   * 本質尊貴（七顆古典行星才有；其餘點位為空）：廟、旺、三分性、界、面，以及落、陷 —— 由強到弱，可能同時有幾種（處女座的水星是廟也是旺）。
   * 由呼叫端以 [withDignities] 附上，判準跟著傳入的 [destiny.core.astrology.classical.IEssential]。空清單不輸出（不論 Json 設定），沒附的 DTO 與舊的凍結 JSON 不變
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  val dignities: List<Dignity> = emptyList(),
  /** [dignities] 的顯示名（同順序；語系由 [withDignities] 的 lang 決定，例：廟、旺、三、界、面、落、陷）。空清單不輸出 */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  val dignityLabels: List<String> = emptyList(),
)

@Serializable
data class HoroscopeHouseDto(
  /** 1–12 */
  val id: Int,
  /** 宮頭的絕對黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val cuspDeg: Double,
  /** 宮頭所在星座 */
  val cuspSign: ZodiacSign,
  /** 宮主星（傳統七星） */
  val ruler: AstroPoint,
  val rulerLabel: String,
  /** 現代的共同主星（水瓶：天王星、雙魚：海王星、天蠍：冥王星）；其他星座為 null */
  val coRuler: AstroPoint? = null,
  val coRulerLabel: String? = null,
)

/**
 * 入相位的預告：[planet] 照目前的行進方向（逆行就往回走），在離開所在星座之前，會與 [to] 形成 [aspect]，
 * 還要走 [degrees] 度。對方視為不動 —— 卜卦以「還差幾度」論應期的慣例；不是實際的成形時刻。
 * 已經在容許度內的入相位也會列出（[degrees] 就是距離精準還差多少）。
 */
@Serializable
data class HoroscopeApplicationDto(
  val planet: AstroPoint,
  val to: AstroPoint,
  val aspect: Aspect,
  val glyph: String? = null,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val degrees: Double,
)

/** [planet] 照目前的行進方向，離開所在星座之前會越過第 [house] 宮的宮頭，還要走 [degrees] 度（宮頭視為不動） */
@Serializable
data class HoroscopeCuspIngressDto(
  val planet: AstroPoint,
  val house: Int,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val degrees: Double,
)

/**
 * 互相接納（含混合接納）：[a] 落在 [b] 以 [aIn] 主管的位置（例：[Dignity.TERM] ＝ a 在 b 的界內），
 * [b] 落在 [a] 以 [bIn] 主管的位置（例：[Dignity.RULER] ＝ b 在 a 主管的星座）
 */
@Serializable
data class HoroscopeReceptionDto(
  val a: AstroPoint,
  val aIn: Dignity,
  val b: AstroPoint,
  val bIn: Dignity,
  /** [aIn]／[bIn] 的顯示文字，語系由 [withReceptions] 的 lang 決定（繁中：廟、旺、界…；英文：Ruler、Term…） */
  val aInLabel: String,
  val bInLabel: String,
)

@Serializable
data class HoroscopeAspectDto(
  /** 兩端點；JSON 是 [AstroPoint.nameKey] */
  val from: AstroPoint,
  val to: AstroPoint,
  /** 相位；JSON 是名稱（如 `CONJUNCTION`） */
  val aspect: Aspect,
  /** 該相位的**標準**角度（0/60/90/120/180…），非實測角 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val angle: Double,
  /** 誤差（度） */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  /** 主要相位（[Aspect.Importance.HIGH]）—— renderer 據此決定線寬 */
  val major: Boolean,
  /** true=入相位、false=出相位、null=未知 */
  val applying: Boolean?,
  /** 相位符號（[Aspect.glyph]：☌ ☍ □ △ ⚹…，次要相位為縮寫） */
  val glyph: String = "",
  /** 相位強度（0–1，容許度內約 0.6 起跳；越緊越接近 1）；未知為 null */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val score: Double? = null,
)

@Serializable
data class HoroscopeMetaDto(
  /** 性別；JSON 是 `M`／`F` */
  val gender: Gender,
  /** 上升點的絕對黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val ascDeg: Double,
  /** 天頂的絕對黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val mcDeg: Double,
  /**
   * 日夜盤；判不出來（只知日期、或時辰橫跨日出日落）為 null。
   *
   * mapper 本身不判：判定要看出生時刻精度與太陽視高度，由呼叫端算好以 [withSect] 填入。
   * **不可**由 client 以「太陽在第 7–12 宮」自行推導 —— 沒有宮位時那個推導根本沒有輸入。
   */
  val sect: DayNight? = null,
)

/** 填入日夜盤（[HoroscopeMetaDto.sect]）。 */
fun HoroscopeChartDto.withSect(sect: DayNight?): HoroscopeChartDto = copy(meta = meta.copy(sect = sect))

/**
 * 定位星關係 —— [Graph] 的呈現層投影，星體在 JSON 裡一律是 [AstroPoint.nameKey]。只含傳統七星（[Planet.classicalList]）。
 *
 * 圖已在 server 算好（[IHoroscopeModel.getGraph]）；client 只需把相鄰兩顆連成箭頭，不必重跑圖演算法。
 * 每條鏈與每個環都依**定位方向**排列：前一顆由後一顆定位（落在後一顆守護的星座）。
 */
@Serializable
data class HoroscopeDispositorDto(
  /** 循環：每顆由下一顆定位，最後一顆由第一顆定位 */
  val circles: List<List<AstroPoint>>,
  /** 最長的定位鏈；終點是 [terminals] 之一，或某個 [circles] 上的星 */
  val paths: List<List<AstroPoint>>,
  /** 最終定位星：落在自己守護的星座，鏈條在此收斂 */
  val terminals: List<AstroPoint>,
  /** 落在自己守護的星座、但沒有其他星由它定位 */
  val isolated: List<AstroPoint>,
) {
  /** 圖上出現的每一顆星 */
  val allPoints: List<AstroPoint> get() = circles.flatten() + paths.flatten() + terminals + isolated
}

/** 格局的一條邊：兩端、相位與實際容許度（server 依兩星的黃經算好，renderer 不必自己算角距） */
@Serializable
data class HoroscopePatternLegDto(
  val from: AstroPoint,
  val to: AstroPoint,
  /** 相位；JSON 是名稱 */
  val aspect: Aspect,
  val glyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
)

/**
 * 格局（[AstroPattern] 的呈現層投影）。判定已在 server 做完（格局的容許度比 [HoroscopeChartDto.aspects] 寬，
 * 所以邊不一定在 aspects 裡）；renderer 照 [legs] 畫線即可。恆星不參與（輪盤不畫恆星）。
 */
@Serializable
data class HoroscopePatternDto(
  /**
   * GRAND_TRINE、KITE、T_SQUARE、YOD、BOOMERANG、GOLDEN_YOD、GRAND_CROSS、DOUBLE_T、HEXAGON、
   * WEDGE、MYSTIC_RECTANGLE、PENTAGRAM、STELLIUM_SIGN、STELLIUM_HOUSE、CONFRONTATION
   */
  val type: HoroscopePatternType,
  /** 全部成員；JSON 是 [AstroPoint.nameKey] */
  val points: List<AstroPoint>,
  /** 頂點：T 三角的四分點、Yod／回力鏢的頂點、黃金 Yod 的指向點、風箏的頭、楔形的調停者 */
  val apex: AstroPoint? = null,
  /** 大三角的元素（[Element]） */
  val element: Element? = null,
  /** 大十字的三方性（[Quality]） */
  val quality: Quality? = null,
  /** 星群的星座／宮位 */
  val sign: ZodiacSign? = null,
  val house: Int? = null,
  val legs: List<HoroscopePatternLegDto>,
  /** 組合格局（雙 T、六芒星、回力鏢）的組成：[HoroscopeChartDto.patterns] 的索引 */
  val parts: List<Int> = emptyList(),
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val score: Double? = null,
  /**
   * 群星聚集（STELLIUM_SIGN、STELLIUM_HOUSE）的成員跨了幾度：成員（四軸除外）的黃經排序後，整圈扣掉最大的那段空隙（跨過 0° 也對）。
   * 其餘格局為 null。衍生欄位，定義與填法見 [withDerivedDegrees]；null 不輸出
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val span: Double? = null,
)

/** 中點：[a]／[b] 的中點落在 [zDeg]，[focal] 就在那裡（容許度 [orb]） */
@Serializable
data class HoroscopeMidpointDto(
  val a: AstroPoint,
  val b: AstroPoint,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val zDeg: Double,
  val focal: AstroPoint,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
)

/**
 * 古典占星的結構化事實（[destiny.core.astrology.classical.ClassicalFact]）的呈現層投影。JSON 以 `kind` 區分種類。
 * [members]：牽涉到的點（[HoroscopePointDto.point]），全部在盤上才會出現
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface HoroscopeClassicalFactDto {
  val members: List<AstroPoint>
}

/**
 * 光線傳遞（[destiny.core.astrology.classical.TranslationOfLight]）：
 * [by] 剛離開 [from] 的 [fromAspect]（出相位 [fromOrb]），下一個成形的是與 [to] 的 [toAspect]（入相位 [toOrb]）。
 * [between]：[from] 與 [to] 本身的相位，null ＝ 彼此不成相位；[arrivalDays]：光在幾天後送到（引擎判定時才有）
 */
@Serializable
@SerialName("translation")
data class HoroscopeTranslationDto(
  val by: AstroPoint,
  val from: AstroPoint,
  val fromAspect: Aspect,
  /** [fromAspect] 的符號（[Aspect.glyph]） */
  val fromGlyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val fromOrb: Double,
  val to: AstroPoint,
  val toAspect: Aspect,
  val toGlyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val toOrb: Double,
  val between: Aspect? = null,
  val betweenGlyph: String? = null,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val arrivalDays: Double? = null,
  /**
   * [from] 與 [to] 兩端在盤面上的實際角距（0–180）。衍生欄位，定義與填法見 [withDerivedDegrees]；null 不輸出。
   * 兩位小數（見 [withDerivedDegrees]）；圖上標一位小數時由它四捨五入
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  val endsGap: Double? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(by, from, to)
}

/**
 * 光線收集（[destiny.core.astrology.classical.CollectionOfLight]）：[a]、[b] 彼此不相見，都入相位到比較慢的 [by]。
 * [between]：[a] 與 [b] 本身的相位，null ＝ 不成相位；[aReception]／[bReception]：以哪一種 [destiny.core.astrology.classical.Dignity] 接納 [by]，null ＝ 不接納
 */
@Serializable
@SerialName("collection")
data class HoroscopeCollectionDto(
  val by: AstroPoint,
  val a: AstroPoint,
  val aAspect: Aspect,
  val aGlyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val aOrb: Double,
  val b: AstroPoint,
  val bAspect: Aspect,
  val bGlyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val bOrb: Double,
  val between: Aspect? = null,
  val betweenGlyph: String? = null,
  val aReception: destiny.core.astrology.classical.Dignity? = null,
  val bReception: destiny.core.astrology.classical.Dignity? = null,
  /**
   * [a] 與 [b] 兩顆快星在盤面上的實際角距（0–180）。衍生欄位，定義與填法見 [withDerivedDegrees]；null 不輸出。
   * 兩位小數（見 [withDerivedDegrees]）；圖上標一位小數時由它四捨五入
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  val endsGap: Double? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(by, a, b)
}

/** 臨陣退縮軌跡的一個取樣點：盤面時刻後 [days] 日，退縮者 [byLng] 與對方 [otherLng] 的黃經 */
@Serializable
data class HoroscopeRefranationSampleDto(
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val days: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val byLng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val otherLng: Double,
)

/**
 * 臨陣退縮（[destiny.core.astrology.classical.ApplicationOutcome.Refranation]）：[by] 與 [other] 正以 [aspect] 入相位（盤面容許度 [orb]），
 * 成形前 [by] 在 [stationDays] 日後停滯（[station]：[StationaryType]），[closestDays] 日後最接近、仍差 [closestOrb] 度。
 * [track]：到兩星再度拉開為止的黃經等距取樣；[ingress]：事件之前換星座的那顆
 */
@Serializable
@SerialName("refranation")
data class HoroscopeRefranationDto(
  val by: AstroPoint,
  val other: AstroPoint,
  val aspect: Aspect,
  val glyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  val station: StationaryType,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val stationDays: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val closestDays: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val closestOrb: Double,
  val track: List<HoroscopeRefranationSampleDto>,
  val ingress: AstroPoint? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(by, other)
}

/**
 * 插隊／撲空軌跡的一個取樣點：盤面時刻後 [days] 日，追的那顆 [applyingLng]、被追的那顆 [appliedLng]
 * 與第三顆星（插隊者／撲空者，即事實的 `by`）[byLng] 的黃經
 */
@Serializable
data class HoroscopeOutcomeSampleDto(
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val days: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val applyingLng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val appliedLng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val byLng: Double,
)

/**
 * 插隊（[destiny.core.astrology.classical.ApplicationOutcome.Prohibition]）：[applying] 正以 [aspect] 入相位 [applied]（盤面容許度 [orb]），
 * 成形之前 [by] 先以 [byAspect] 與 [with] 成形（[eventDays] 日後）。[byAspect] 為 CONJUNCTION ＝ 以星體攔截，否則透過光線
 */
@Serializable
@SerialName("prohibition")
data class HoroscopeProhibitionDto(
  val applying: AstroPoint,
  val applied: AstroPoint,
  val aspect: Aspect,
  val glyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  val by: AstroPoint,
  val with: AstroPoint,
  val byAspect: Aspect,
  val byGlyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val eventDays: Double,
  val track: List<HoroscopeOutcomeSampleDto>,
  val ingress: AstroPoint? = null,
  /**
   * 第三顆星先成形的那一刻（[eventDays] 日後），[applying] 與 [applied] 離 [aspect] 還差幾度 —— 原本的相位就停在這個差距落空。
   * 衍生欄位，定義與填法見 [withDerivedDegrees]；[track] 是空的就算不出來（null），null 不輸出
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val gapAtEvent: Double? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(applying, applied, by)
}

/**
 * 撲空（[destiny.core.astrology.classical.ApplicationOutcome.Frustration]）：[applying] 正以 [aspect] 入相位 [applied]（盤面容許度 [orb]），
 * 成形之前 [applied] 先以 [byAspect] 與更慢的 [by] 成形然後離開（[eventDays] 日後）
 */
@Serializable
@SerialName("frustration")
data class HoroscopeFrustrationDto(
  val applying: AstroPoint,
  val applied: AstroPoint,
  val aspect: Aspect,
  val glyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  val by: AstroPoint,
  val byAspect: Aspect,
  val byGlyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val eventDays: Double,
  val track: List<HoroscopeOutcomeSampleDto>,
  val ingress: AstroPoint? = null,
  /**
   * 第三顆星先成形的那一刻（[eventDays] 日後），[applying] 與 [applied] 離 [aspect] 還差幾度 —— 原本的相位就停在這個差距落空。
   * 衍生欄位，定義與填法見 [withDerivedDegrees]；[track] 是空的就算不出來（null），null 不輸出
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val gapAtEvent: Double? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(applying, applied, by)
}

/** 包夾的一側：[by] 的星體（[aspect] 為 null）或以 [aspect] 投射的光線，離被夾的星 [distance] 度 */
@Serializable
data class HoroscopeBesiegeSideDto(
  val by: AstroPoint,
  val aspect: Aspect? = null,
  /** [aspect] 的符號；星體那一側為 null */
  val glyph: String? = null,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val distance: Double,
)

/** 包夾（[destiny.core.astrology.classical.Besiegement]）：[planet] 被 [before]（黃經較小的一側）與 [after] 夾住 */
@Serializable
@SerialName("besiegement")
data class HoroscopeBesiegementDto(
  val planet: AstroPoint,
  val before: HoroscopeBesiegeSideDto,
  val after: HoroscopeBesiegeSideDto,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet, before.by, after.by)
}

/**
 * 居上（[destiny.core.astrology.classical.Overcoming]，以星座計）：[superior] 在 [inferior] 的右方，以 [aspect] 居上；
 * 四分（SQUARE）即 decimation。[orb]：實際角距與 [aspect] 標準角度的差；[byDegree]：度數上也在容許度內成相位
 */
@Serializable
@SerialName("overcoming")
data class HoroscopeOvercomingDto(
  val superior: AstroPoint,
  val inferior: AstroPoint,
  val aspect: Aspect,
  val glyph: String? = null,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  val byDegree: Boolean,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(superior, inferior)
}

/**
 * 不相見（[destiny.core.astrology.classical.Aversion]，以星座計）：[planet] 落在 [from] 所在星座起算的第 [place] 個星座（2、6、8、12），彼此看不到。
 * [basis]：RULER ＝ [from] 是 [planet] 所在星座的主人；ASCENDANT ＝ [planet] 是命主星、[from] 是上升點
 */
@Serializable
@SerialName("aversion")
data class HoroscopeAversionDto(
  val planet: AstroPoint,
  val from: AstroPoint,
  val place: Int,
  val basis: destiny.core.astrology.classical.Aversion.Basis,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet, from)
}

/** 映點（[destiny.core.astrology.classical.Antiscion]）：[planet] 與 [other] 互為映點（[contra] ＝ 反映點），相差 [orb] 度 */
@Serializable
@SerialName("antiscia")
data class HoroscopeAntisciaDto(
  val planet: AstroPoint,
  val other: AstroPoint,
  val contra: Boolean,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  /**
   * [planet] 離對稱軸幾度。對稱軸取離 [planet] 較近的那一端：映點是夏至（90°）或冬至（270°），反映點是春分（0°）或秋分（180°）。
   * 衍生欄位，定義與填法見 [withDerivedDegrees]；null 不輸出
   */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val planetOffAxis: Double? = null,
  /** [other] 離同一個軸端（[planetOffAxis] 量的那一端）幾度；與 [planetOffAxis] 的差就是 [orb]（捨位誤差之內） */
  @OptIn(ExperimentalSerializationApi::class)
  @EncodeDefault(EncodeDefault.Mode.NEVER)
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val otherOffAxis: Double? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet, other)
}

/**
 * 持矛者（[destiny.core.astrology.classical.Doryphoria]）：[bearer] 護衛光體 [light]，相距 [distance] 度；
 * [preceding]：在光體前方（先升起）；[inSect]：與盤同區分（水星或日夜不明為 null）
 */
@Serializable
@SerialName("doryphoria")
data class HoroscopeDoryphoriaDto(
  val light: AstroPoint,
  val bearer: AstroPoint,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val distance: Double,
  val preceding: Boolean,
  val inSect: Boolean? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(light, bearer)
}

/**
 * 灼傷（[destiny.core.astrology.classical.Combustion]）：[planet] 與太陽相距 [distance]，落在 [zone] 這一圈
 * （日心 CAZIMI、灼傷 COMBUST、光束下 UNDER_BEAMS）；[approaching]：正往太陽靠近為 true、正在遠離為 false，速度不明為 null。
 * [cazimiLimit]／[combustLimit]／[beamsLimit]：判定時用的三圈外緣（度）—— renderer 照它畫三圈，不要自己寫死數字
 */
@Serializable
@SerialName("combustion")
data class HoroscopeCombustionDto(
  val planet: AstroPoint,
  val zone: destiny.core.astrology.classical.Combustion.Zone,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val distance: Double,
  val approaching: Boolean? = null,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val cazimiLimit: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val combustLimit: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val beamsLimit: Double,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet, Planet.SUN)
}

/**
 * 區分（[destiny.core.astrology.classical.Sect]）：盤的日夜 [sect]、區分光體 [light]、同隊 [inSect]／異隊 [contrary]（含光體；水星依東出西入歸隊），
 * 同區分的吉星 [beneficOfSect]、異區分的凶星 [maleficContrary]。一張盤至多一筆，盤的日夜不明時不出現
 */
@Serializable
@SerialName("sect")
data class HoroscopeSectDto(
  val sect: DayNight,
  val light: AstroPoint,
  val inSect: List<AstroPoint>,
  val contrary: List<AstroPoint>,
  val beneficOfSect: AstroPoint,
  val maleficContrary: AstroPoint,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = inSect + contrary
}

/**
 * 得時（[destiny.core.astrology.classical.Hayz]）：[planet] 的晝夜歸屬 [planetSect]、在地平線上（[aboveHorizon]，以上升點黃經判）、
 * 所在星座的陰陽（[masculineSign]）三者都合宜。[chartSect]：盤的日夜；[wantMasculine]：這顆星要的陰陽
 */
@Serializable
@SerialName("hayz")
data class HoroscopeHayzDto(
  val planet: AstroPoint,
  val chartSect: DayNight,
  val planetSect: DayNight,
  val aboveHorizon: Boolean,
  val sign: ZodiacSign,
  val masculineSign: Boolean,
  val wantMasculine: Boolean,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet)
}

/**
 * 顯現（[destiny.core.astrology.classical.Phasis]）：[planet] 在出生前後 [days] 天（負數 ＝ 出生前）有一次 [type] 的轉折，
 * 那一刻在黃經 [lng]，位於太陽東方（晨星）為 [oriental]；[beams]：出入光束時的光束半徑（度），停滯為 null
 */
@Serializable
@SerialName("phasis")
data class HoroscopePhasisDto(
  val planet: AstroPoint,
  val type: destiny.core.astrology.classical.Phasis.Type,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val days: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val lng: Double,
  val oriental: Boolean,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val beams: Double? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet)
}

/** 野生（[destiny.core.astrology.classical.Feral]）：[planet] 與其餘古典行星都沒有相位；[basis]：以星芒容許度或以星座判 */
@Serializable
@SerialName("feral")
data class HoroscopeFeralDto(
  val planet: AstroPoint,
  val basis: destiny.core.astrology.classical.FeralOptions.Basis,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet)
}

/** 月亮離與合的一端：與 [planet] 的 [aspect]，盤面時刻離準確 [orb] 度、距準確 [days] 天 */
@Serializable
data class HoroscopeLunarLinkDto(
  val planet: AstroPoint,
  val aspect: Aspect,
  val glyph: String?,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val days: Double,
)

/** 月亮的離與合（[destiny.core.astrology.classical.LunarConnection]）：最後離開的 [separating]、下一個入的 [applying]（至少一個） */
@Serializable
@SerialName("lunar_connection")
data class HoroscopeLunarConnectionDto(
  val separating: HoroscopeLunarLinkDto? = null,
  val applying: HoroscopeLunarLinkDto? = null,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(Planet.MOON) + listOfNotNull(separating?.planet, applying?.planet)
}

/** 燃燒路（[destiny.core.astrology.classical.ViaCombusta]）：[planet] 在黃經 [lng]，落在 [from]（含）到 [to]（不含）那一段（天秤後半到天蠍前半） */
@Serializable
@SerialName("via_combusta")
data class HoroscopeViaCombustaDto(
  val planet: AstroPoint,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val lng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val from: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val to: Double,
) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = listOf(planet)
}

/** 受剋／受扶的一條條件；JSON 以 `type` 區分 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface HoroscopeMaltreatmentConditionDto {
  val by: List<AstroPoint>

  /** 被 [superior] 以 [aspect] 居上 */
  @Serializable @SerialName("overcome")
  data class Overcome(val superior: AstroPoint, val aspect: Aspect, val glyph: String? = null) : HoroscopeMaltreatmentConditionDto {
    override val by: List<AstroPoint> get() = listOf(superior)
  }

  /** 與 [source] 對沖；[orb]：與 180° 的差 */
  @Serializable @SerialName("opposed")
  data class Opposed(val source: AstroPoint, @Serializable(with = DoubleTwoDecimalSerializer::class) val orb: Double) : HoroscopeMaltreatmentConditionDto {
    override val by: List<AstroPoint> get() = listOf(source)
  }

  /** 與 [source] 貼合（合相） */
  @Serializable @SerialName("adherence")
  data class Adherence(val source: AstroPoint, @Serializable(with = DoubleTwoDecimalSerializer::class) val orb: Double) : HoroscopeMaltreatmentConditionDto {
    override val by: List<AstroPoint> get() = listOf(source)
  }

  /** 被 [source] 以 [aspect] 的光線打中；[dexter]：光線逆黃道方向投射 */
  @Serializable @SerialName("ray")
  data class Ray(val source: AstroPoint, val aspect: Aspect, val glyph: String? = null,
                 @Serializable(with = DoubleTwoDecimalSerializer::class) val orb: Double, val dexter: Boolean) : HoroscopeMaltreatmentConditionDto {
    override val by: List<AstroPoint> get() = listOf(source)
  }

  /** 被 [before]、[after] 包夾 */
  @Serializable @SerialName("besieged")
  data class Besieged(val before: HoroscopeBesiegeSideDto, val after: HoroscopeBesiegeSideDto) : HoroscopeMaltreatmentConditionDto {
    override val by: List<AstroPoint> get() = listOf(before.by, after.by)
  }

  /** 最近一次離開與 [from] 的 [fromAspect]（已過 [fromOrb] 度），下一個迎上與 [to] 的 [toAspect]（還差 [toOrb] 度） */
  @Serializable @SerialName("sequence")
  data class Sequence(
    val from: AstroPoint, val fromAspect: Aspect, val fromGlyph: String? = null,
    @Serializable(with = DoubleTwoDecimalSerializer::class) val fromOrb: Double,
    val to: AstroPoint, val toAspect: Aspect, val toGlyph: String? = null,
    @Serializable(with = DoubleTwoDecimalSerializer::class) val toOrb: Double,
  ) : HoroscopeMaltreatmentConditionDto {
    override val by: List<AstroPoint> get() = listOf(from, to)
  }
}

/** 受剋（[destiny.core.astrology.classical.Maltreatment]，施予者是凶星）：[planet] 符合的 [conditions] */
@Serializable
@SerialName("maltreatment")
data class HoroscopeMaltreatmentDto(val planet: AstroPoint, val conditions: List<HoroscopeMaltreatmentConditionDto>) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = (listOf(planet) + conditions.flatMap { it.by }).distinct()
}

/** 受扶（施予者是吉星）：與 [HoroscopeMaltreatmentDto] 同一套條件 */
@Serializable
@SerialName("bonification")
data class HoroscopeBonificationDto(val planet: AstroPoint, val conditions: List<HoroscopeMaltreatmentConditionDto>) : HoroscopeClassicalFactDto {
  override val members: List<AstroPoint> get() = (listOf(planet) + conditions.flatMap { it.by }).distinct()
}

@Serializable
data class HoroscopeChartDto(
  val meta: HoroscopeMetaDto,
  val points: List<HoroscopePointDto>,
  /** 12 宮頭。順序不保證，client 依 [HoroscopeHouseDto.id] 對位 */
  val houses: List<HoroscopeHouseDto>,
  /** 相位線。兩端點必定都在 [points] 內 */
  val aspects: List<HoroscopeAspectDto>,
  /** 定位星關係；來源盤沒有算、或有星不在 [points] 內時為 null（舊的凍結 JSON 也沒有） */
  val dispositors: HoroscopeDispositorDto? = null,
  /** 格局；成員都在 [points] 內 */
  val patterns: List<HoroscopePatternDto> = emptyList(),
  /** 有焦點星的中點；三顆都在 [points] 內 */
  val midpoints: List<HoroscopeMidpointDto> = emptyList(),
  /** 古典占星的結構化事實（光線傳遞、收集、臨陣退縮、插隊、撲空、包夾、居上、不相見、受剋、受扶、持矛者、映點、灼傷、區分、得時、顯現、野生、月亮的離與合、燃燒路）；成員都在 [points] 內。由呼叫端以 [withClassicalFacts] 附上 */
  val classicalFacts: List<HoroscopeClassicalFactDto> = emptyList(),
  /** 各行星離開所在星座前的入相位（依 planet、再依 degrees 排序）；兩端都在 [points] 內，不含 LOT */
  val applications: List<HoroscopeApplicationDto> = emptyList(),
  /** 各行星離開所在星座前會越過的宮頭（依 planet、再依 degrees 排序）；沒有宮位時為空 */
  val cuspIngresses: List<HoroscopeCuspIngressDto> = emptyList(),
  /** 行星之間的互相接納；由呼叫端以 [withReceptions] 附上 */
  val receptions: List<HoroscopeReceptionDto> = emptyList(),
)

/**
 * 附上行星之間的互相接納（[dignities] 之內，含不同 dignity 的混合接納）；有星不在盤上的略過。
 * [positions] 是各點的黃道位置；界（TERM）要靠度數，所以不能只給星座。[lang] 決定 [HoroscopeReceptionDto.aInLabel]／[HoroscopeReceptionDto.bInLabel] 的語系
 */
fun HoroscopeChartDto.withReceptions(
  essential: destiny.core.astrology.classical.IEssential,
  positions: Map<AstroPoint, ZodiacDegree>,
  dignities: Set<Dignity> = setOf(Dignity.RULER, Dignity.EXALTATION, Dignity.TERM),
  lang: Lang = ZH,
): HoroscopeChartDto {
  val keys = points.mapTo(HashSet()) { it.point }
  val planets = positions.filterKeys { it is Planet && it in keys }
  val list = essential.getMutualReceptionMap(planets, null, dignities)
    .map { md ->
      // MutualData：p 以 dignityMap[p] 接納另一顆 ⇒ 另一顆落在 p 的那個 dignity 裡
      val (p1, p2) = md.twoPoints.sortedBy { it.nameKey }
      val aIn = md.getDignityOf(p2)
      val bIn = md.getDignityOf(p1)
      HoroscopeReceptionDto(
        a = p1, aIn = aIn, b = p2, bIn = bIn,
        aInLabel = aIn.getTitle(lang), bInLabel = bIn.getTitle(lang),
      )
    }
    .distinct()
    .sortedWith(compareBy({ it.a.nameKey }, { it.b.nameKey }, { it.aIn }, { it.bIn }))
  return copy(receptions = list)
}

/**
 * 附上七顆古典行星的本質尊貴（[HoroscopePointDto.dignities]）。判準由 [essential] 決定（本命用的那一份：托勒密的廟旺落陷、三分性、界、面）；
 * [positions] 是各點的黃道位置（界、面要看度數）；三分性要看日夜，取 [sect]（預設 [HoroscopeMetaDto.sect]）。
 * 兩者都沒有時，只有日夜同一顆主星的三分性才算（見 [destiny.core.astrology.classical.IEssential.getDignities]）。[lang] 決定 [HoroscopePointDto.dignityLabels] 的語系
 */
fun HoroscopeChartDto.withDignities(
  essential: destiny.core.astrology.classical.IEssential,
  positions: Map<AstroPoint, ZodiacDegree>,
  sect: DayNight? = meta.sect,
  lang: Lang = ZH,
): HoroscopeChartDto {
  val degrees: Map<AstroPoint, Double> = positions.mapValues { (_, d) -> d.value }
  return copy(points = points.map { p ->
    val planet = (p.point as? Planet)?.takeIf { it in Planet.classicalList } ?: return@map p
    val dignities = with(essential) { planet.getDignities(degrees, sect) }
    p.copy(dignities = dignities, dignityLabels = dignities.map { it.getTitle(lang) })
  })
}

/**
 * 附上古典占星的結構化事實（[destiny.core.astrology.classical.ClassicalFact]）；有星不在盤上的略過。
 * 成形（[destiny.core.astrology.classical.ApplicationOutcome.Perfected]）不附 —— 那就是一般的入相位，相位線已經畫了。
 * 事實的衍生度數（[withDerivedDegrees]）在這裡一併填好
 */
fun HoroscopeChartDto.withClassicalFacts(facts: List<destiny.core.astrology.classical.ClassicalFact>): HoroscopeChartDto {
  val keys = points.mapTo(HashSet()) { it.point }
  return copy(classicalFacts = facts.mapNotNull { it.toChartDto() }.filter { f -> f.members.all { it in keys } }).withDerivedDegrees()
}

/**
 * 呈現層投影；成形（[destiny.core.astrology.classical.ApplicationOutcome.Perfected]）沒有投影，回 null。
 * 單獨一筆事實看不到整張盤，衍生度數留空 —— 要它們就走 [withClassicalFacts]，或對組好的盤呼叫 [withDerivedDegrees]
 */
fun destiny.core.astrology.classical.ClassicalFact.toChartDto(): HoroscopeClassicalFactDto? = when (this) {
  is destiny.core.astrology.classical.TranslationOfLight -> HoroscopeTranslationDto(
    by = translator,
    from = from, fromAspect = separating.aspect, fromGlyph = separating.aspect.glyph, fromOrb = separating.orb,
    to = to, toAspect = applying.aspect, toGlyph = applying.aspect.glyph, toOrb = applying.orb,
    between = between?.aspect, betweenGlyph = between?.aspect?.glyph,
    arrivalDays = arrival?.eventDays,
  )

  is destiny.core.astrology.classical.CollectionOfLight -> {
    val (pa, pb) = planets
    HoroscopeCollectionDto(
      by = collector,
      a = pa, aAspect = first.aspect, aGlyph = first.aspect.glyph, aOrb = first.orb,
      b = pb, bAspect = second.aspect, bGlyph = second.aspect.glyph, bOrb = second.orb,
      between = between?.aspect, betweenGlyph = between?.aspect?.glyph,
      aReception = firstReception,
      bReception = secondReception,
    )
  }

  is destiny.core.astrology.classical.ApplicationOutcome.Perfected -> null

  is destiny.core.astrology.classical.ApplicationOutcome.Refranation -> HoroscopeRefranationDto(
    by = refranator, other = other,
    aspect = pattern.aspect, glyph = pattern.aspect.glyph, orb = pattern.orb,
    station = stationary.type, stationDays = stationDays,
    closestDays = closestDays, closestOrb = closestOrb,
    track = track.map {
      if (refranator === applying) HoroscopeRefranationSampleDto(it.days, it.applyingLng, it.appliedLng)
      else HoroscopeRefranationSampleDto(it.days, it.appliedLng, it.applyingLng)
    },
    ingress = ingress,
  )

  is destiny.core.astrology.classical.ApplicationOutcome.Prohibition -> HoroscopeProhibitionDto(
    applying = applying, applied = applied, aspect = pattern.aspect, glyph = pattern.aspect.glyph, orb = pattern.orb,
    by = prohibitor, with = with, byAspect = interruption.aspect, byGlyph = interruption.aspect.glyph, eventDays = eventDays,
    track = track.map { it.toChartDto() },
    ingress = ingress,
  )

  is destiny.core.astrology.classical.ApplicationOutcome.Frustration -> HoroscopeFrustrationDto(
    applying = applying, applied = applied, aspect = pattern.aspect, glyph = pattern.aspect.glyph, orb = pattern.orb,
    by = frustrator, byAspect = interruption.aspect, byGlyph = interruption.aspect.glyph, eventDays = eventDays,
    track = track.map { it.toChartDto() },
    ingress = ingress,
  )

  is destiny.core.astrology.classical.Besiegement -> HoroscopeBesiegementDto(
    planet = planet,
    before = before.toChartDto(),
    after = after.toChartDto(),
  )

  is destiny.core.astrology.classical.Aversion -> HoroscopeAversionDto(planet, from, place, basis)

  is destiny.core.astrology.classical.Antiscion -> HoroscopeAntisciaDto(planet, other, contra, orb)

  is destiny.core.astrology.classical.Doryphoria -> HoroscopeDoryphoriaDto(light, bearer, distance, preceding, inSect)

  is destiny.core.astrology.classical.Sect -> HoroscopeSectDto(sect, light, inSect, contrary, beneficOfSect, maleficContrary)

  is destiny.core.astrology.classical.Hayz -> HoroscopeHayzDto(planet, chartSect, planetSect, aboveHorizon, sign, masculineSign, wantMasculine)

  is destiny.core.astrology.classical.Combustion -> HoroscopeCombustionDto(planet, zone, distance, approaching, limits.cazimi, limits.combust, limits.underBeams)

  is destiny.core.astrology.classical.Phasis -> HoroscopePhasisDto(planet, type, days, lngDeg.value, oriental, beams)

  is destiny.core.astrology.classical.Feral -> HoroscopeFeralDto(planet, basis)

  is destiny.core.astrology.classical.LunarConnection -> HoroscopeLunarConnectionDto(separating?.toChartDto(), applying?.toChartDto())

  is destiny.core.astrology.classical.ViaCombusta -> HoroscopeViaCombustaDto(planet, lngDeg.value, from, to)

  is destiny.core.astrology.classical.Maltreatment -> {
    val list = conditions.map { it.toChartDto() }
    if (polarity == destiny.core.astrology.classical.Maltreatment.Polarity.MALTREATMENT) HoroscopeMaltreatmentDto(planet, list)
    else HoroscopeBonificationDto(planet, list)
  }

  is destiny.core.astrology.classical.Overcoming -> HoroscopeOvercomingDto(
    superior = superior, inferior = inferior,
    aspect = aspect, glyph = aspect.glyph, orb = orb, byDegree = byDegree,
  )
}

private fun destiny.core.astrology.classical.Maltreatment.Condition.toChartDto(): HoroscopeMaltreatmentConditionDto = when (this) {
  is destiny.core.astrology.classical.Maltreatment.Condition.Overcome ->
    HoroscopeMaltreatmentConditionDto.Overcome(overcoming.superior, overcoming.aspect, overcoming.aspect.glyph)
  is destiny.core.astrology.classical.Maltreatment.Condition.Opposed -> HoroscopeMaltreatmentConditionDto.Opposed(source, orb)
  is destiny.core.astrology.classical.Maltreatment.Condition.Adherence -> HoroscopeMaltreatmentConditionDto.Adherence(source, orb)
  is destiny.core.astrology.classical.Maltreatment.Condition.RayHurled ->
    HoroscopeMaltreatmentConditionDto.Ray(source, aspect, aspect.glyph, orb, dexter)
  is destiny.core.astrology.classical.Maltreatment.Condition.Besieged ->
    HoroscopeMaltreatmentConditionDto.Besieged(besiegement.before.toChartDto(), besiegement.after.toChartDto())
  is destiny.core.astrology.classical.Maltreatment.Condition.Sequence ->
    HoroscopeMaltreatmentConditionDto.Sequence(from, fromAspect, fromAspect.glyph, fromOrb, to, toAspect, toAspect.glyph, toOrb)
}

/** 包夾的一側 */
private fun destiny.core.astrology.classical.Besiegement.Side.toChartDto() = HoroscopeBesiegeSideDto(by, aspect, aspect?.glyph, distance)

/** 插隊／撲空的取樣點；這兩種結局的軌跡一定帶第三顆星的位置 */
private fun destiny.core.astrology.classical.OutcomeSample.toChartDto() =
  HoroscopeOutcomeSampleDto(days, applyingLng, appliedLng, checkNotNull(interrupterLng) { "插隊／撲空的軌跡缺第三顆星的黃經" })

/** 拿掉動畫軌跡（存表、查詢用不到，一條 49 點） */
fun HoroscopeClassicalFactDto.withoutTrack(): HoroscopeClassicalFactDto = when (this) {
  is HoroscopeRefranationDto -> copy(track = emptyList())
  is HoroscopeProhibitionDto -> copy(track = emptyList())
  is HoroscopeFrustrationDto -> copy(track = emptyList())
  is HoroscopeTranslationDto, is HoroscopeCollectionDto, is HoroscopeBesiegementDto, is HoroscopeOvercomingDto, is HoroscopeAversionDto,
  is HoroscopeMaltreatmentDto, is HoroscopeBonificationDto, is HoroscopeDoryphoriaDto, is HoroscopeAntisciaDto, is HoroscopeCombustionDto,
  is HoroscopeSectDto, is HoroscopeHayzDto, is HoroscopePhasisDto, is HoroscopeFeralDto, is HoroscopeLunarConnectionDto,
  is HoroscopeViaCombustaDto -> this
}

private fun destiny.core.astrology.classical.LunarConnection.Link.toChartDto() =
  HoroscopeLunarLinkDto(planet, aspect, aspect.glyph, orb, days)

/** 格局可能用到的相位；組合格局（雙 T、六芒星、回力鏢）另外併入組成部分的邊 */
private val AstroPattern.legAspects: Set<Aspect>
  get() = when (this) {
    is AstroPattern.GrandTrine      -> setOf(Aspect.TRINE)
    is AstroPattern.Kite            -> setOf(Aspect.TRINE, Aspect.OPPOSITION, Aspect.SEXTILE)
    is AstroPattern.TSquared        -> setOf(Aspect.OPPOSITION, Aspect.SQUARE)
    is AstroPattern.Yod             -> setOf(Aspect.SEXTILE, Aspect.QUINCUNX)
    is AstroPattern.Boomerang       -> setOf(Aspect.OPPOSITION, Aspect.SEMISEXTILE)
    is AstroPattern.GoldenYod       -> setOf(Aspect.QUINTILE, Aspect.BIQUINTILE)
    is AstroPattern.GrandCross      -> setOf(Aspect.OPPOSITION, Aspect.SQUARE)
    is AstroPattern.DoubleT         -> emptySet()
    is AstroPattern.Hexagon         -> setOf(Aspect.SEXTILE, Aspect.OPPOSITION)
    is AstroPattern.Wedge           -> setOf(Aspect.OPPOSITION, Aspect.TRINE, Aspect.SEXTILE)
    is AstroPattern.MysticRectangle -> setOf(Aspect.OPPOSITION, Aspect.TRINE, Aspect.SEXTILE)
    is AstroPattern.Pentagram       -> setOf(Aspect.QUINTILE, Aspect.BIQUINTILE)
    is AstroPattern.StelliumSign,
    is AstroPattern.StelliumHouse   -> emptySet()
    is AstroPattern.Confrontation   -> setOf(Aspect.OPPOSITION)
  }

private val AstroPattern.apexPoint: AstroPoint?
  get() = when (this) {
    is AstroPattern.TSquared  -> squared.point
    is AstroPattern.Yod       -> apex.point
    is AstroPattern.Boomerang -> oppoPoint.point
    is AstroPattern.GoldenYod -> pointer.point
    is AstroPattern.Kite      -> head.point
    is AstroPattern.Wedge     -> mediator.point
    else                      -> null
  }

private val AstroPattern.partPatterns: List<AstroPattern>
  get() = when (this) {
    is AstroPattern.DoubleT   -> tSquares.toList()
    is AstroPattern.Hexagon   -> grandTrines.toList()
    is AstroPattern.Boomerang -> listOf(yod)
    else                      -> emptyList()
  }

/** 格局的邊判到哪一個相位時，允許實際角距偏離多少（格局的容許度上限之外再留餘裕） */
private const val LEG_TOLERANCE = 12.0

/** [patterns] → 呈現層投影；有成員不在盤上（或是恆星）的格局，連同用到它的組合格局一起略過 */
private fun List<AstroPattern>.toPatternDtos(zDegOf: (AstroPoint) -> Double?, visibleKeys: Set<AstroPoint>): List<HoroscopePatternDto> {
  val out = mutableListOf<HoroscopePatternDto>()
  val indexOf = mutableMapOf<AstroPattern, Int?>()

  fun legsAmong(points: List<AstroPoint>, aspects: Set<Aspect>): List<HoroscopePatternLegDto> =
    points.flatMapIndexed { i, a -> points.drop(i + 1).map { b -> a to b } }.mapNotNull { (a, b) ->
      val za = zDegOf(a) ?: return@mapNotNull null
      val zb = zDegOf(b) ?: return@mapNotNull null
      aspects.map { it to it.orbOf(za, zb) }
        .filter { (_, orb) -> orb <= LEG_TOLERANCE }
        .minByOrNull { (_, orb) -> orb }
        ?.let { (aspect, orb) -> HoroscopePatternLegDto(a, b, aspect, aspect.glyph, orb) }
    }

  fun add(pattern: AstroPattern): Int? = indexOf.getOrPut(pattern) {
    val members = pattern.points.toList()
    if (members.any { it is FixedStar || it !in visibleKeys }) return@getOrPut null
    val parts = pattern.partPatterns.map { add(it) ?: return@getOrPut null }
    val partLegs = parts.flatMap { out[it].legs }
    val ownLegs = legsAmong(members, pattern.legAspects)
      .filter { leg -> partLegs.none { setOf(it.from, it.to) == setOf(leg.from, leg.to) } }
    out += HoroscopePatternDto(
      type = pattern.type,
      points = members,
      apex = pattern.apexPoint,
      element = (pattern as? AstroPattern.GrandTrine)?.element,
      quality = (pattern as? AstroPattern.GrandCross)?.quality,
      sign = (pattern as? AstroPattern.StelliumSign)?.sign,
      house = (pattern as? AstroPattern.StelliumHouse)?.house,
      legs = partLegs + ownLegs,
      parts = parts,
      score = pattern.score?.value,
    )
    out.size - 1
  }

  forEach { add(it) }
  return out
}


/** [Graph] → 呈現層投影；圖是空的（沒有算）或有星不在盤面上時回 null */
private fun Graph<Planet>.toDispositorDto(visibleKeys: Set<AstroPoint>): HoroscopeDispositorDto? {
  val planets = circles.flatMap { it.toList() } + paths.flatten() + terminals + isolated
  if (planets.isEmpty() || planets.any { it !in visibleKeys }) return null
  return HoroscopeDispositorDto(
    circles = circles.map { c -> c.toList() },
    paths = paths.toList(),
    terminals = terminals.toList(),
    isolated = isolated.toList(),
  )
}

/** 該點位是否在此密度下呈現 */
private fun AstroPoint.visibleIn(density: ChartDensity): Boolean = when (density) {
  ChartDensity.COMPACT      -> this is Planet
  ChartDensity.FULL,
  ChartDensity.ALL          -> true
}

/** 有性別的盤（[Natal]／[NatalV2] 皆可）；[lang] 見另一個多載 */
fun IPersonHoroscopeDto.toHoroscopeChartDto(density: ChartDensity, lang: Lang = ZH): HoroscopeChartDto =
  toHoroscopeChartDto(density, gender, lang)

/**
 * 只讀 [stars]、[houses]、[tightestAspects] —— V1 的 [IHoroscopeDto] 就夠，不必先轉 V2。
 * 盤本身不帶性別時（非 person 的 [IHoroscopeDto]），由呼叫端給。
 *
 * [lang] 只決定顯示文字：點位的 `label`／`abbr`、宮主星的 `rulerLabel`／`coRulerLabel`。
 * 識別欄位（點位、星座、相位）與字形不分語系，`abbrEn` 永遠是英文。
 */
fun IHoroscopeDto.toHoroscopeChartDto(density: ChartDensity, gender: Gender, lang: Lang = ZH): HoroscopeChartDto {
  val houseDtos = houses.map { h ->
    HoroscopeHouseDto(
      id = h.id,
      cuspDeg = h.cusp.zDeg,
      cuspSign = h.cusp.sign,
      ruler = h.ruler,
      rulerLabel = h.ruler.getTitle(lang),
      coRuler = MODERN_CO_RULER[h.cusp.sign],
      coRulerLabel = MODERN_CO_RULER[h.cusp.sign]?.getTitle(lang),
    )
  }

  /**
   * 上升／天頂優先取 [Axis] 的實際位置，僅在缺漏時退回宮頭。
   * 不能一律用宮頭 —— 整宮制（Whole Sign）的第一宮頭是上升星座的 0°，不等於上升度數。
   */
  fun axisDeg(axis: Axis, fallbackHouseId: Int): Double =
    stars[axis]?.signDegree?.zDeg
      ?: houseDtos.firstOrNull { it.id == fallbackHouseId }?.cuspDeg
      ?: 0.0

  val pointDtos = stars.entries
    .filter { (point, _) -> point.visibleIn(density) }
    .map { (point, info) ->
      HoroscopePointDto(
        point = point,
        label = point.getTitle(lang),
        glyph = point.unicode?.toString(),
        zDeg = info.signDegree.zDeg,
        sign = info.signDegree.sign,
        signDegree = info.signDegree.signDegree.second,
        house = info.house,
        retrograde = info.isRetrograde,
        kind = point.kind,
        abbr = point.getAbbreviation(lang),
        abbrEn = point.getAbbreviation(Lang.EN),
        declination = info.declination,
        retrogradePhase = info.retrogradePhase,
        speed = info.speed,
      )
    }

  val majorAspects = Aspect.getAspects(Aspect.Importance.HIGH)
  val visibleKeys = pointDtos.map { it.point }.toSet()

  return HoroscopeChartDto(
    meta = HoroscopeMetaDto(
      gender = gender,
      ascDeg = axisDeg(Axis.RISING, 1),
      mcDeg = axisDeg(Axis.MERIDIAN, 10),
    ),
    points = pointDtos,
    houses = houseDtos,
    aspects = tightestAspects.mapNotNull { pattern ->
      val ends = pattern.points.take(2)
      if (ends.size < 2) return@mapNotNull null
      // 兩端點都必須在盤面上，否則會畫出連到空氣的線
      if (ends.any { it !in visibleKeys }) return@mapNotNull null
      val aspect = Aspect.getAspect(pattern.angle) ?: return@mapNotNull null
      val major = aspect in majorAspects
      // 次要相位只在 ALL 出現 —— 全開會把圓盤糊成一團
      if (!major && density != ChartDensity.ALL) return@mapNotNull null

      HoroscopeAspectDto(
        from = ends[0],
        to = ends[1],
        aspect = aspect,
        glyph = aspect.glyph,
        angle = aspect.degree,
        orb = pattern.orb,
        major = major,
        applying = pattern.aspectType?.let { it == IPointAspectPattern.AspectType.APPLYING },
        score = pattern.score?.value,
      )
    },
    dispositors = graphPatterns.toDispositorDto(visibleKeys),
    patterns = astroPatterns.toPatternDtos({ stars[it]?.signDegree?.zDeg }, visibleKeys),
    midpoints = midPoints
      .filter { m -> (m.points + m.focal).all { it !is FixedStar && it in visibleKeys } }
      .map { m -> HoroscopeMidpointDto(m.p1, m.p2, m.degree.zDeg, m.focal, m.orb) },
    applications = applicationsOf(pointDtos),
    cuspIngresses = cuspIngressesOf(pointDtos, houseDtos),
  ).withDerivedDegrees()
}

/** 現代的共同主星 */
private val MODERN_CO_RULER: Map<ZodiacSign, Planet> =
  mapOf(ZodiacSign.AQUARIUS to Planet.URANUS, ZodiacSign.PISCES to Planet.NEPTUNE, ZodiacSign.SCORPIO to Planet.PLUTO)

/** 主要相位（合、六合、四分、三分、對沖） */
private val MAJOR_ANGLES: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH)

/** 入相位的標的：行星、交點、軸點（恆星、小行星、阿拉伯點不算） */
private val APPLICATION_TARGET_KINDS: Set<PointKind> = setOf(PointKind.PLANET, PointKind.NODE, PointKind.AXIS)

/** 會走的星：行星，且有速度 */
private fun movers(points: List<HoroscopePointDto>) =
  points.filter { it.kind == PointKind.PLANET && (it.speed ?: 0.0) != 0.0 }

/**
 * [p] 沿行進方向走到黃經 [target] 要幾度；超過所在星座的邊界（順行到 30°、逆行到 0°）就是 null。
 * 正好在 [target] 上（還不到 0.005°）也算 null —— 那是已經成形，不是「還要走」
 */
private fun degreesAhead(p: HoroscopePointDto, target: Double): Double? {
  val forward = (p.speed ?: 0.0) > 0
  val d = if (forward) target.aheadOf(p.zDeg) else p.zDeg.aheadOf(target)
  val room = if (forward) 30 - p.signDegree else p.signDegree
  return d.takeIf { it > 0.005 && it <= room }
}

private fun applicationsOf(points: List<HoroscopePointDto>): List<HoroscopeApplicationDto> {
  val targets = points.filter { it.kind in APPLICATION_TARGET_KINDS }
  return movers(points).flatMap { m ->
    targets.filter { it.point != m.point }.flatMap { t ->
      MAJOR_ANGLES.flatMap { aspect ->
        aspect.rays(t.zDeg).mapNotNull { c ->
          degreesAhead(m, c)?.let { HoroscopeApplicationDto(m.point, t.point, aspect, aspect.glyph, it) }
        }
      }
    }.sortedBy { it.degrees }
  }
}

private fun cuspIngressesOf(points: List<HoroscopePointDto>, houses: List<HoroscopeHouseDto>): List<HoroscopeCuspIngressDto> =
  movers(points).flatMap { m ->
    houses.mapNotNull { h -> degreesAhead(m, h.cuspDeg)?.let { HoroscopeCuspIngressDto(m.point, h.id, it) } }.sortedBy { it.degrees }
  }

/**
 * 只留 [keep] 的點位，並維持各欄位「成員都在 [HoroscopeChartDto.points] 內」的契約：
 * 相位、中點只留端點都在的；格局有成員被拿掉就略過（用到它的組合格局一起略過，索引重排）；
 * 定位星關係只要有一顆被拿掉就整個拿掉（星座不可靠，整張圖都不可靠）。
 */
fun HoroscopeChartDto.keepingPoints(keep: (HoroscopePointDto) -> Boolean): HoroscopeChartDto {
  val kept = points.filter(keep)
  val keys = kept.mapTo(HashSet()) { it.point }
  val newIndex = mutableMapOf<Int, Int>()
  val keptPatterns = mutableListOf<HoroscopePatternDto>()
  patterns.forEachIndexed { i, p ->
    if (p.points.all { it in keys } && p.parts.all { it in newIndex }) {
      newIndex[i] = keptPatterns.size
      keptPatterns += p.copy(parts = p.parts.map { newIndex.getValue(it) })
    }
  }
  return copy(
    points = kept,
    aspects = aspects.filter { it.from in keys && it.to in keys },
    dispositors = dispositors?.takeIf { d -> d.allPoints.all { it in keys } },
    patterns = keptPatterns,
    midpoints = midpoints.filter { it.a in keys && it.b in keys && it.focal in keys },
    classicalFacts = classicalFacts.filter { f -> f.members.all { it in keys } },
    applications = applications.filter { it.planet in keys && it.to in keys },
    cuspIngresses = cuspIngresses.filter { it.planet in keys },
    receptions = receptions.filter { it.a in keys && it.b in keys },
  )
}

/**
 * 依出生時刻精度拿掉算不準的部分，判準同 [BirthDataGrain.allowsNatalTarget]：
 * - 沒有分鐘（[BirthDataGrain.includeAxis] 為 false）⇒ 軸點、宮位、各點所在的宮位。
 *   上升／天頂度數歸零 —— 0° 是 renderer 對「沒有上升」的退路，而不是一個捏造的上升。
 * - 沒有時辰 ⇒ 再拿掉月亮。
 *
 * 相位、格局、中點、定位星關係的連帶處理見 [keepingPoints]。
 */
fun HoroscopeChartDto.gatedBy(grain: BirthDataGrain): HoroscopeChartDto {
  val gated: Set<AstroPoint> = grain.gatedNatalTargets().toSet()
  val kept = keepingPoints { it.point !in gated }
  return if (grain.includeAxis) kept
  else kept.copy(
    meta = meta.copy(ascDeg = 0.0, mcDeg = 0.0),
    points = kept.points.map { it.copy(house = null) },
    houses = emptyList(),
    cuspIngresses = emptyList(),
    // 星群的宮位與宮位星群本身都不可得
    patterns = kept.patterns.map { it.copy(house = null) },
  )
}
