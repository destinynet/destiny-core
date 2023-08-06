/**
 * Created by smallufo on 2015-06-03.
 */
package destiny.core.calendar

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.EclipseTime
import destiny.core.astrology.eclipse.LunarType
import destiny.core.astrology.eclipse.SolarType
import destiny.core.chinese.Branch
import destiny.tools.getTitle
import java.util.*


sealed class TimeDesc(override val begin : GmtJulDay,
                      open val descs: List<String>) : IEvent {

  constructor(begin: GmtJulDay, desc: String) : this(begin, listOf(desc))

  /** 時辰開始 */
  data class TypeHour(override val begin: GmtJulDay,
                      val b: Branch,
                      val lunarStation: LunarStation?,
                      override val descs: List<String>) : TimeDesc(begin, descs)


  /** 日、月 四個至點 */
  data class TypeTransPoint(override val begin: GmtJulDay,
                            val desc: String,
                            val point: AstroPoint,
                            val tp: TransPoint) : TimeDesc(begin, listOf(desc))

  /** 節氣 */
  data class TypeSolarTerms(override val begin: GmtJulDay,
                            val desc: String,
                            val solarTerms: SolarTerms) : TimeDesc(begin, listOf(desc))

  /** 日月交角 */
  data class TypeSunMoon(override val begin: GmtJulDay,
                         val phase: LunarPhase) : TimeDesc(begin, phase.getTitle(Locale.getDefault()))

  /** 日食 */
  data class TypeSolarEclipse(override val begin: GmtJulDay,
                              val type: SolarType,
                              val time: EclipseTime,
                              val locPlace: ILocationPlace? = null) :
    TimeDesc(begin, buildString {
      append(type.getTitle(Locale.getDefault()))
      append(" ")
      time.getTitle(Locale.getDefault())
    }.let {
      locPlace?.let { lp ->
        it + " 於 " + lp.place
      } ?: it
    }
    )

  /** 月食 */
  data class TypeLunarEclipse(override val begin: GmtJulDay,
                              val type: LunarType,
                              val time: EclipseTime
  ) : TimeDesc(
    begin, when (type) {
      LunarType.PARTIAL -> LunarType.PARTIAL.getTitle(Locale.getDefault()) + " " + time.getTitle(Locale.getDefault())
      LunarType.TOTAL -> LunarType.TOTAL.getTitle(Locale.getDefault()) + time.getTitle(Locale.getDefault())
      LunarType.PENUMBRA -> LunarType.PENUMBRA.getTitle(Locale.getDefault()) + time.getTitle(Locale.getDefault())
    }
  )


  sealed class VoidMoon(override val begin: GmtJulDay,
                        desc: String) : TimeDesc(begin, desc) {

    /** 月空亡開始 */
    data class Begin(val voidCourse: Misc.VoidCourse, val loc: ILocation) : VoidMoon(
      voidCourse.begin,
      "月空亡開始，剛離開與 ${voidCourse.exactAspectPrior.points.first { it != Planet.MOON }} 的 ${voidCourse.exactAspectPrior.aspect.getTitle(Locale.TAIWAN)} "
    )

    /** 月空亡結束 */
    data class End(val voidCourse: Misc.VoidCourse, val loc: ILocation) : VoidMoon(
      voidCourse.end, "月空亡結束"
    )
  }


  companion object {
    val julDayResolver = JulDayResolver1582CutoverImpl()
  }
}



