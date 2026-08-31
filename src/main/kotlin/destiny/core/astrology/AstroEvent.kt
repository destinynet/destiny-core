package destiny.core.astrology

import destiny.core.IAggregatedEvent
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipse
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IZodiacDegreeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 占星事件 */
@Serializable
sealed class AstroEvent : IAggregatedEvent {

  /** 交角 */
  @Serializable
  @SerialName("Astro.AspectEvent")
  data class AspectEvent(
    override val description: String,
    val aspectData: AspectData,
    /**
     * true ＝ **行運星互相位**（sky-to-sky，`AstrologyTraversalConfig.globalAspect`）——
     * [aspectData] 的兩端**都是行運端**，沒有任何本命側資訊。
     *
     * ## ⚠️ 為什麼需要一個旗標，而不是靠呼叫端自己記得
     *
     * 消費端普遍假設 `aspectData.points[0]` 是行運端、`[1]` 是本命端
     * （`OccasionCorpus.toOccasion` 就是這樣投影的）。對互相位事件，那個假設會把
     * `t.Uranus` 當成**本命**天王星記進母體 —— 一筆帶收據的假資料，且不會有任何錯誤訊息。
     *
     * 在 2026-08-31 之前，這件事的安全性靠一個**遠端的旗標**維持：長期背景掃描
     * 剛好 `globalAspect = false`。那不是不變式，是巧合 —— 有人為了別的理由打開它，
     * 母體就靜默地被汙染。旗標讓判斷變成**本地**的。
     *
     * 帶預設值 false：既有已序列化的資料沒有這個欄位（與 `zodiacDegree`、
     * `transitToNatalAspects` 補上時同一個理由）。
     */
    val global: Boolean = false,
    /**
     * 該時刻**兩顆行運星各自**對本命各點的相位 —— 只有 [global] 為真時才有值。
     *
     * ## 為什麼互相位需要這個
     *
     * 事件本身只說「兩顆行運星成相」（`[transiting Mars] CONJUNCTION [transiting Uranus]`）。
     * 而讀者真正要問的是「它壓在我的哪個本命點上」—— 素材不印那一刻的黃經，
     * 讀者無從複合，只能憑記憶回想那時天王星在哪。憑記憶回答天象正是本專案要消滅的行為。
     *
     * 語意與 [SignIngress]／`StationaryMoment` 的接點完全相同（**各自**對本命成相），
     * 不是「合相中點壓在本命點上」—— 後者只在合相有意義，六分／三分時是另一回事。
     * 本命側同樣過 `allowsNatalTarget` 閘門。
     *
     * ⚠️ 計算方式**不建整張盤**：取兩顆星的黃經（兩次 starPosition）對本命位置 map 求相位。
     * 2026-08-31 實測過相反做法的代價 —— 為每個事件建盤讓一段掃描從 290ms 變成 1m10s。
     */
    val transitToNatalAspects: List<SynastryAspect> = emptyList(),
  ) : AstroEvent()

  /** 月亮空亡 */
  @Serializable
  @SerialName("Astro.MoonVoc")
  data class MoonVoc(
    override val description: String,
    val voidCourseSpan: Misc.VoidCourseSpan
  ) : AstroEvent()

  /** 星體滯留 */
  @Serializable
  @SerialName("Astro.PlanetStationary")
  data class PlanetStationary(
    override val description: String,
    val stationary: Stationary,
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : AstroEvent()

  /** 當日星體逆行 */
  @Serializable
  @SerialName("Astro.PlanetRetrograde")
  data class PlanetRetrograde(
    override val description: String,
    val planet: Planet,
    @Serializable(with = DoubleTwoDecimalSerializer::class)
    val progress: Double
  ) : AstroEvent()

  /** 日食 or 月食 */
  @Serializable
  @SerialName("Astro.Eclipse")
  data class Eclipse(
    override val description: String,
    val eclipse: IEclipse,
    val transitToNatalAspects: List<SynastryAspect>,
    /**
     * 食點的黃道度數。
     *
     * ⚠️ 2026-08-28 補上 —— 此前 [Eclipse] 是三個帶位置的事件型別裡**唯一沒存度數的**
     * （[PlanetStationary] 與 [LunarPhaseEvent] 都有）。度數只出現在 [description]
     * 的字串裡（`… at Libra/19.02°`），於是任何想用它做計算的消費端都只能去
     * 正則解析我們自己渲染的字串。
     *
     * 可為 null 且有預設值（與兩個兄弟不同）：既有已序列化的資料裡沒有這個欄位，
     * 給預設值才不會讓它們反序列化失敗。
     */
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree? = null,
  ) : AstroEvent()

  /** 月相 */
  @Serializable
  @SerialName("Astro.LunarPhaseEvent")
  data class LunarPhaseEvent(
    override val description: String,
    val phase: LunarPhase,
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : AstroEvent()

  /** 星體換星座 */
  @Serializable
  @SerialName("Astro.SignIngress")
  data class SignIngress(
    override val description: String,
    val astroPoint: AstroPoint,
    val oldSign: ZodiacSign,
    val newSign: ZodiacSign,
    /**
     * 此時刻過運星體與本命星體形成的黃道相位。
     *
     * ⚠️ **必須有預設值**：既有已序列化的資料沒有這個欄位，無預設值會讓反序列化失敗
     * （與 [Eclipse.zodiacDegree] 2026-08-28 補上時同一個理由）。
     *
     * 本命側已過 `BirthDataGrain.allowsNatalTarget` 閘門 —— DAY grain 不含本命月亮與軸點。
     */
    val transitToNatalAspects: List<SynastryAspect> = emptyList(),
  ) : AstroEvent()

  /** 星體換宮位 */
  @Serializable
  @SerialName("Astro.HouseIngress")
  data class HouseIngress(
    override val description: String,
    val astroPoint: AstroPoint,
    val oldHouse: Int,
    val newHouse: Int,
    /**
     * 此時刻過運星體與本命星體形成的黃道相位。
     *
     * ⚠️ **必須有預設值**：既有已序列化的資料沒有這個欄位，無預設值會讓反序列化失敗
     * （與 [Eclipse.zodiacDegree] 2026-08-28 補上時同一個理由）。
     *
     * 本命側已過 `BirthDataGrain.allowsNatalTarget` 閘門 —— DAY grain 不含本命月亮與軸點。
     */
    val transitToNatalAspects: List<SynastryAspect> = emptyList(),
  ) : AstroEvent()

  /** 星體進入或離開 Out of Bounds (赤緯超過 ±obliquity) */
  @Serializable
  @SerialName("Astro.OobIngress")
  data class OobIngress(
    override val description: String,
    val star: Star,
    /** true = entering OOB, false = leaving OOB */
    val entering: Boolean,
    /** declination at crossing moment (positive or negative) */
    @Serializable(with = DoubleTwoDecimalSerializer::class)
    val declination: Double,
    /** 此時刻過運星體與本命星體形成的赤緯相位（parallel / contra-parallel） */
    val natalParallels: List<DeclinationAspect> = emptyList(),
    /**
     * 此時刻過運星體與本命星體形成的黃道相位。
     *
     * ⚠️ 與 [natalParallels] **兩者都給**（使用者 2026-08-30 裁定）：
     * OOB 是赤緯現象，但同一時刻的黃道接觸同樣有判讀價值，兩種座標不互相取代。
     *
     * ⚠️ **必須有預設值**：既有已序列化的資料沒有這個欄位，無預設值會讓反序列化失敗
     * （與 [Eclipse.zodiacDegree] 2026-08-28 補上時同一個理由）。
     *
     * 本命側已過 `BirthDataGrain.allowsNatalTarget` 閘門 —— DAY grain 不含本命月亮與軸點。
     */
    val transitToNatalAspects: List<SynastryAspect> = emptyList(),
  ) : AstroEvent()

}
