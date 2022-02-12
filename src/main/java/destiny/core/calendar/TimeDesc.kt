/**
 * Created by smallufo on 2015-06-03.
 */
package destiny.core.calendar

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.ILunarEclipse
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

enum class EclipseTime {
  BEGIN,
  MAX,
  END;


  fun desc(): String {
    return when (this) {
      BEGIN -> "開始"
      MAX -> "食甚"
      END -> "結束"
    }
  }
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
                            val point: Point,
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
                              val type: ISolarEclipse.SolarType,
                              val time: EclipseTime,
                              val locPlace: ILocationPlace? = null) : TimeDesc(lmt, when (type) {
    ISolarEclipse.SolarType.PARTIAL -> "日偏食 " + time.desc()
    ISolarEclipse.SolarType.TOTAL -> "日全食 " + time.desc()
    ISolarEclipse.SolarType.ANNULAR -> "日環食 " + time.desc()
    ISolarEclipse.SolarType.HYBRID -> "全環食 " + time.desc()
  }.let {
    locPlace?.let { lp ->
      it + " 於 " + lp.place
    } ?: it
  })

  /** 月食 */
  data class TypeLunarEclipse(override val lmt: LocalDateTime,
                              val type: ILunarEclipse.LunarType,
                              val time: EclipseTime) : TimeDesc(lmt, when (type) {
    ILunarEclipse.LunarType.PARTIAL -> "月偏食 " + time.desc()
    ILunarEclipse.LunarType.TOTAL -> "月全食 " + time.desc()
    ILunarEclipse.LunarType.PENUMBRA -> "半影月食 " + time.desc()
  })


  sealed class VoidMoon(override val lmt: LocalDateTime , desc: String) : TimeDesc(lmt , desc) {

    /** 月空亡開始 */
    data class Begin(val voidCourse: Misc.VoidCourse, val loc: ILocation) : VoidMoon(
      TimeTools.getLmtFromGmt(voidCourse.beginGmt, loc, julDayResolver) as LocalDateTime,
      "月空亡開始，剛離開與 ${voidCourse.exactAspectPrior.points.first { it != Planet.MOON }} 的 ${voidCourse.exactAspectPrior.aspect.toString(Locale.TAIWAN)} "
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



