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
    val transitToNatalAspects: List<SynastryAspect>
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

}
