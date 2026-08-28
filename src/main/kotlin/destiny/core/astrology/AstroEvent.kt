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
    val aspectData: AspectData
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
  ) : AstroEvent()

  /** 星體換宮位 */
  @Serializable
  @SerialName("Astro.HouseIngress")
  data class HouseIngress(
    override val description: String,
    val astroPoint: AstroPoint,
    val oldHouse: Int,
    val newHouse: Int,
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
  ) : AstroEvent()

}
