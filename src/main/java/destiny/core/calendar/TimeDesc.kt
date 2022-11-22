/**
 * Created by smallufo on 2015-06-03.
 */
package destiny.core.calendar

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.LunarStation
import destiny.core.astrology.Planet
import destiny.core.astrology.TransPoint
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.LunarType
import destiny.core.astrology.eclipse.SolarType
import destiny.core.chinese.Branch
import destiny.tools.getTitle
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

enum class EclipseTime {
  BEGIN,
  MAX,
  END;
}


sealed class TimeDesc(open val lmt: LocalDateTime,
                      open val descs: List<String>) : Serializable, Comparable<TimeDesc> {

  constructor(lmt: LocalDateTime, desc: String) : this(lmt, listOf(desc))

  override fun compareTo(other: TimeDesc): Int {
    return when {
      lmt.isBefore(other.lmt) -> -1
      lmt.isAfter(other.lmt) -> 1
      else -> 0
    }
  }

  /** 時辰開始 */
  data class TypeHour(override val lmt: LocalDateTime,
                      val b: Branch,
                      val lunarStation: LunarStation?,
                      override val descs: List<String>) : TimeDesc(lmt, descs)


  /** 日、月 四個至點 */
  data class TypeTransPoint(override val lmt: LocalDateTime,
                            val desc: String,
                            val point: AstroPoint,
                            val tp: TransPoint) : TimeDesc(lmt, listOf(desc))

  /** 節氣 */
  data class TypeSolarTerms(override val lmt: LocalDateTime,
                            val desc: String,
                            val solarTerms: SolarTerms) : TimeDesc(lmt, listOf(desc))

  /** 日月交角 */
  data class TypeSunMoon(override val lmt: LocalDateTime,
                         val desc: String,
                         val degree: Int) : TimeDesc(lmt, listOf(desc))

  /** 日食 */
  data class TypeSolarEclipse(override val lmt: LocalDateTime,
                              val type: SolarType,
                              val time: EclipseTime,
                              val locPlace: ILocationPlace? = null) : TimeDesc(lmt, when (type) {
    SolarType.PARTIAL -> SolarType.PARTIAL.getTitle(Locale.getDefault()) + " " + time.getTitle(Locale.getDefault())
    SolarType.TOTAL -> SolarType.TOTAL.getTitle(Locale.getDefault()) + " " + time.getTitle(Locale.getDefault())
    SolarType.ANNULAR -> SolarType.ANNULAR.getTitle(Locale.getDefault())+ " " + time.getTitle(Locale.getDefault())
    SolarType.HYBRID  -> SolarType.HYBRID.getTitle(Locale.getDefault()) + " "+ time.getTitle(Locale.getDefault())
  }.let {
    locPlace?.let { lp ->
      it + " 於 " + lp.place
    } ?: it
  })

  /** 月食 */
  data class TypeLunarEclipse(override val lmt: LocalDateTime,
                              val type: LunarType,
                              val time: EclipseTime) : TimeDesc(lmt, when (type) {
    LunarType.PARTIAL -> LunarType.PARTIAL.getTitle(Locale.getDefault()) + " " + time.getTitle(Locale.getDefault())
    LunarType.TOTAL -> LunarType.TOTAL.getTitle(Locale.getDefault()) + time.getTitle(Locale.getDefault())
    LunarType.PENUMBRA -> LunarType.PENUMBRA.getTitle(Locale.getDefault()) + time.getTitle(Locale.getDefault())
  })


  sealed class VoidMoon(override val lmt: LocalDateTime , desc: String) : TimeDesc(lmt , desc) {

    /** 月空亡開始 */
    data class Begin(val voidCourse: Misc.VoidCourse, val loc: ILocation) : VoidMoon(
      TimeTools.getLmtFromGmt(voidCourse.beginGmt, loc, julDayResolver) as LocalDateTime,
      "月空亡開始，剛離開與 ${voidCourse.exactAspectPrior.points.first { it != Planet.MOON }} 的 ${voidCourse.exactAspectPrior.aspect.getTitle(Locale.TAIWAN)} "
    )

    /** 月空亡結束 */
    data class End(val voidCourse: Misc.VoidCourse, val loc: ILocation) : VoidMoon(
      TimeTools.getLmtFromGmt(voidCourse.endGmt, loc, julDayResolver) as LocalDateTime, "月空亡結束"
    )
  }


  companion object {
    val julDayResolver = JulDayResolver1582CutoverImpl()
  }
}



