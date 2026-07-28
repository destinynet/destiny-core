package destiny.core.astrology

import destiny.core.IAggregatedEvent
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IZodiacDegreeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** [AstroEvent.AspectPeak] 在影響區間三事件中扮演的角色 */
@Serializable
enum class PeakRole { ENTER, PEAK, LEAVE }

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

  /**
   * 慢速推運（SA / SP）相位的 peakOrb 影響區間事件。
   * 一個精準相位發射三筆：ENTER（進入 peakOrb）、PEAK（精準）、LEAVE（脫離 peakOrb），
   * 三筆都攜帶完整的 [enter]/[peak]/[leave] 時刻，供逐月閱讀者在任一月份看見完整區間。
   *
   * 時刻的座標系由生產者決定：SA 填真實時間、SP 填 convergent 時間；
   * SP 的值由 ReportFactory.fetchEvents 以 getDivergentTime 映射為真實時間後，
   * 連同含日期的 description 一併改寫 —— 下游永遠只見真實時間。
   */
  @Serializable
  @SerialName("Astro.AspectPeak")
  data class AspectPeak(
    override val description: String,
    val aspectData: AspectData,
    val role: PeakRole,
    val peakOrb: Double,
    /** 進入 peakOrb 的時刻；被搜尋視窗裁掉時為 null（單邊顯示） */
    val enter: GmtJulDay?,
    /** 相位精準時刻 */
    val peak: GmtJulDay,
    /** 脫離 peakOrb 的時刻；被搜尋視窗裁掉時為 null（單邊顯示） */
    val leave: GmtJulDay?,
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
