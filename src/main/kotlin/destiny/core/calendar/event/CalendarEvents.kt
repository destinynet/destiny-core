/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core.calendar.event

import destiny.core.astrology.Aspect
import destiny.core.astrology.LunarPhase
import destiny.core.astrology.OutOfBoundsPhase
import destiny.core.astrology.Planet
import destiny.core.astrology.RetrogradeCycle
import destiny.core.astrology.RetrogradePhase
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.eclipse.LunarType
import destiny.core.astrology.eclipse.SolarType
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.IAnchoredEvent
import destiny.core.calendar.IEventSpan
import destiny.core.calendar.SolarTerms
import destiny.core.toString
import destiny.tools.Lang
import destiny.tools.getTitle
import destiny.tools.serializers.astrology.PlanetSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 事件曆上的一件事：屬於某個系列 [type]，以 [anchor] 識別。
 *
 * 全部是可序列化的 data class，與地點無關（地心、全球同一時刻），時刻一律 UT。
 */
@Serializable
sealed interface ICalendarEvent : IAnchoredEvent {
  val type: CalendarEventType
}

/**
 * 一次逆行週期：進入陰影 → 停滯轉逆 → 停滯轉順 → 離開陰影。錨點是停滯轉逆。
 *
 * 經度只存黃經（逆行與陰影區都只看黃經）。
 */
@Serializable
@SerialName("Retrograde")
data class RetrogradeCycleEvent(
  @Serializable(with = PlanetSerializer::class)
  val planet: Planet,
  /** 進入陰影（開始準備逆行） */
  val preparing: GmtJulDay,
  val preparingLng: ZodiacDegree,
  /** 停滯轉逆 */
  val retrograding: GmtJulDay,
  val retrogradingLng: ZodiacDegree,
  /** 停滯轉順 */
  val returning: GmtJulDay,
  val returningLng: ZodiacDegree,
  /** 離開陰影 */
  val leaving: GmtJulDay,
  val leavingLng: ZodiacDegree,
) : ICalendarEvent, IEventSpan {

  override val type: CalendarEventType get() = CalendarEventType.Retrograde(planet)
  override val begin: GmtJulDay get() = preparing
  override val end: GmtJulDay get() = leaving
  override val anchor: GmtJulDay get() = retrograding

  /** [gmtJulDay] 落在週期的哪一相；週期之外回 null */
  fun phaseAt(gmtJulDay: GmtJulDay): RetrogradePhase? = when {
    gmtJulDay < preparing    -> null
    gmtJulDay < retrograding -> RetrogradePhase.PREPARING
    gmtJulDay < returning    -> RetrogradePhase.RETROGRADING
    gmtJulDay < leaving      -> RetrogradePhase.LEAVING
    else                     -> null
  }

  /**
   * 黃經 [lng] 是否落在陰影區（停滯轉順的度數 ～ 停滯轉逆的度數）。
   * 陰影區可能跨 0°（雙魚 → 牡羊），交給 [ZodiacDegree.between] 處理。
   */
  fun inShadowZone(lng: ZodiacDegree): Boolean = lng.between(returningLng, retrogradingLng)

  override fun getTitle(lang: Lang): String = planet.toString(lang) + RetrogradePhase.RETROGRADING.getTitle(lang)

  companion object {
    fun of(cycle: RetrogradeCycle): RetrogradeCycleEvent {
      val planet = requireNotNull(cycle.star as? Planet) { "only planets: ${cycle.star}" }
      return RetrogradeCycleEvent(
        planet,
        cycle.preparingGmt, cycle.preparingPos.lngDeg,
        cycle.retrogradingGmt, cycle.retrogradingPos.lngDeg,
        cycle.returningGmt, cycle.returningPos.lngDeg,
        cycle.leavingGmt, cycle.leavingPos.lngDeg,
      )
    }
  }
}

/**
 * 一次日食（全球）。錨點是食甚。
 * [centerLat]／[centerLng]：食甚時中心線所在的地點；偏食沒有中心線，為 null。
 */
@Serializable
@SerialName("SolarEclipse")
data class SolarEclipseEvent(
  val solarType: SolarType,
  override val begin: GmtJulDay,
  val max: GmtJulDay,
  override val end: GmtJulDay,
  /** 食甚時太陽（＝月亮）的黃經 */
  val sunLng: ZodiacDegree,
  val centerLat: Double? = null,
  val centerLng: Double? = null,
) : ICalendarEvent, IEventSpan {
  override val type: CalendarEventType get() = CalendarEventType.SolarEclipse
  override val anchor: GmtJulDay get() = max

  override fun getTitle(lang: Lang): String = solarType.getTitle(lang)
}

/** 一次月食（全球）。錨點是食甚 */
@Serializable
@SerialName("LunarEclipse")
data class LunarEclipseEvent(
  val lunarType: LunarType,
  override val begin: GmtJulDay,
  val max: GmtJulDay,
  override val end: GmtJulDay,
  /** 食甚時月亮的黃經（太陽在對面） */
  val moonLng: ZodiacDegree,
) : ICalendarEvent, IEventSpan {
  override val type: CalendarEventType get() = CalendarEventType.LunarEclipse
  override val anchor: GmtJulDay get() = max

  override fun getTitle(lang: Lang): String = lunarType.getTitle(lang)
}

/** 朔、上弦、望、下弦：月亮對太陽成 0°／90°／180°／270° 的時刻 */
@Serializable
@SerialName("Lunation")
data class LunationEvent(
  val phase: LunarPhase,
  override val anchor: GmtJulDay,
  /** 該時刻月亮的黃經 */
  val moonLng: ZodiacDegree,
) : ICalendarEvent {
  override val type: CalendarEventType get() = CalendarEventType.Lunation(phase)
  override val begin: GmtJulDay get() = anchor

  /** 太陽的黃經：由月相角度推得 */
  val sunLng: ZodiacDegree get() = (moonLng.value - phase.angle).toZodiacDegree()

  override fun getTitle(lang: Lang): String = phase.getTitle(lang)
}

/** 一個節氣：太陽到達 [term] 的度數，持續到下一個節氣 */
@Serializable
@SerialName("SolarTerm")
data class SolarTermEvent(
  val term: SolarTerms,
  override val begin: GmtJulDay,
  override val end: GmtJulDay,
) : ICalendarEvent, IEventSpan {
  override val type: CalendarEventType get() = CalendarEventType.SolarTerm(term)
  override val anchor: GmtJulDay get() = begin

  override fun getTitle(lang: Lang): String = term.getTitle(lang)
}

/**
 * 一次空亡：星體（多半是月亮）做完最後一個主要相位之後、換座之前。錨點是空亡開始。
 * [lastAspect]／[lastAspectWith]：開始空亡前的最後一個相位與對象。
 */
@Serializable
@SerialName("VoidOfCourse")
data class VoidOfCourseEvent(
  @Serializable(with = PlanetSerializer::class)
  val planet: Planet,
  val impl: VoidCourseImpl,
  override val begin: GmtJulDay,
  val beginLng: ZodiacDegree,
  override val end: GmtJulDay,
  val endLng: ZodiacDegree,
  val lastAspect: Aspect? = null,
  @Serializable(with = PlanetSerializer::class)
  val lastAspectWith: Planet? = null,
) : ICalendarEvent, IEventSpan {
  override val type: CalendarEventType get() = CalendarEventType.VoidOfCourse(planet, impl)
  override val anchor: GmtJulDay get() = begin
}

/**
 * 一次出界：赤緯越過黃赤交角 → 最深 → 回到交角之內。錨點是出界。
 *
 * 赤緯帶正負號：正＝北、負＝南。[peakObliquity] 是最深那一刻的交角，所以 [depth] 不必再查交角。
 * 經度只存黃經（同逆行）。
 */
@Serializable
@SerialName("OutOfBounds")
data class OutOfBoundsEvent(
  @Serializable(with = PlanetSerializer::class)
  val planet: Planet,
  /** 出界 */
  override val begin: GmtJulDay,
  val beginLng: ZodiacDegree,
  /** |赤緯| 最大 */
  val peak: GmtJulDay,
  val peakDeclination: Double,
  val peakObliquity: Double,
  val peakLng: ZodiacDegree,
  /** 回界 */
  override val end: GmtJulDay,
  val endLng: ZodiacDegree,
) : ICalendarEvent, IEventSpan {
  override val type: CalendarEventType get() = CalendarEventType.OutOfBounds(planet)
  override val anchor: GmtJulDay get() = begin

  /** 往北（赤緯為正）出界 */
  val north: Boolean get() = peakDeclination > 0

  /** 最深時超出交角多少度 */
  val depth: Double get() = kotlin.math.abs(peakDeclination) - peakObliquity

  /** 某一相的時刻 */
  fun at(phase: OutOfBoundsPhase): GmtJulDay = when (phase) {
    OutOfBoundsPhase.ENTERING -> begin
    OutOfBoundsPhase.PEAK     -> peak
    OutOfBoundsPhase.LEAVING  -> end
  }

  override fun getTitle(lang: Lang): String = planet.toString(lang) + OutOfBoundsPhase.ENTERING.getTitle(lang)
}
