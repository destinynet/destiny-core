/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.astrology.IPlanetaryHour.HourIndexOfDay
import destiny.core.astrology.Planet.SUN
import destiny.core.astrology.TransPoint.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import jakarta.inject.Named
import destiny.tools.KotlinLogging
import java.io.Serializable
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

/**
 * http://www.astrology.com.tr/planetary-hours.asp
 *
 * http://pansci.asia/archives/126644
 *
 * 晝夜、分別劃分 12等分
 */
@Named
class PlanetaryHourAstroImpl(private val riseTransImpl: IRiseTrans) : IPlanetaryHour, Serializable {


  override fun getHourIndexOfDay(gmtJulDay: GmtJulDay, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig): HourIndexOfDay? {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val hour = lmt.get(ChronoField.HOUR_OF_DAY)

    // 極區內可能不適用
    return riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, RISING, loc, transConfig)?.let { nextRising ->
      riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, SETTING, loc, transConfig)?.let { nextSetting ->

        return if (nextRising < nextSetting) {
          // 目前是黑夜
          // 先計算「接近上一個中午」的時刻，這裡不用算得很精準
          val nearPrevMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, MERIDIAN, loc, transConfig)!! - 1 // 記得減一
          // 接著，計算「上一個」日落時刻
          val prevSetting = riseTransImpl.getGmtTransJulDay(nearPrevMeridian, SUN, SETTING, loc, transConfig)!!

          val dayOfWeek = if (hour < 12)
            lmt.minus(12, ChronoUnit.HOURS).get(ChronoField.DAY_OF_WEEK)
          else
            lmt.get(ChronoField.DAY_OF_WEEK)

          getHourIndexOfHalfDay(prevSetting, nextRising, gmtJulDay).let {
            HourIndexOfDay(it.hourStart, it.hourEnd, it.halfIndex + 12, DayNight.NIGHT, dayOfWeek)
          }

        } // 黑夜
        else {
          // 目前是白天
          val dayOfWeek: Int = lmt.get(ChronoField.DAY_OF_WEEK)
          // 先計算「接近上一個子正的時刻」，這禮不用算得很經準
          val nearPrevMidNight = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, NADIR, loc, transConfig)!! - 1 // 記得減一
          // 接著，計算「上一個」日出時刻
          val prevRising = riseTransImpl.getGmtTransJulDay(nearPrevMidNight, SUN, RISING, loc, transConfig)!!

          getHourIndexOfHalfDay(prevRising, nextSetting, gmtJulDay).let {
            HourIndexOfDay(it.hourStart, it.hourEnd, it.halfIndex, DayNight.DAY, dayOfWeek)
          }
        } // 白天

      }
    }
  }


  /**
   * @param halfIndex 「半天」的 hourIndex , 1 to 12
   */
  private data class HourIndexOfHalfDay(val hourStart: GmtJulDay, val hourEnd: GmtJulDay, val halfIndex: Int)

  private fun getHourIndexOfHalfDay(from: GmtJulDay, to: GmtJulDay, gmtJulDay: GmtJulDay): HourIndexOfHalfDay {

    require(gmtJulDay in from..to) {
      "gmtJulDay $gmtJulDay not between $from and $to"
    }

    val avgHour = (to - from) / 12.0

    return (1..11).map { i ->
      val stepFrom = from + avgHour * (i - 1)
      val stepTo = from + avgHour * i
      Triple(stepFrom, stepTo, i)
    }.firstOrNull { (stepFrom, stepTo) -> gmtJulDay >= stepFrom && gmtJulDay < stepTo }
      ?.let { (stepFrom, stepTo, index) -> HourIndexOfHalfDay(stepFrom, stepTo, index) }
      ?: HourIndexOfHalfDay(from + avgHour * 11, to, 12)
  }


  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
