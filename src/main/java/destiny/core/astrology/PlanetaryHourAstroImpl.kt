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
import java.io.Serializable
import javax.inject.Named

/**
 * http://www.astrology.com.tr/planetary-hours.asp
 *
 * http://pansci.asia/archives/126644
 *
 * 晝夜、分別劃分 12等分
 */
@Named
class PlanetaryHourAstroImpl(private val riseTransImpl: IRiseTrans) : IPlanetaryHour, Serializable {


  override fun getHourIndexOfDay(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): HourIndexOfDay? {

    // 極區內可能不適用
    return riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, RISING, loc, transConfig)?.let { nextRising ->
      riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, SETTING, loc, transConfig)?.let { nextSetting ->
        val halfDayIndex: HourIndexOfDay
        val dayNight: DayNight
        if (nextRising < nextSetting) {
          // 目前是黑夜
          dayNight = DayNight.NIGHT
          // 先計算「接近上一個中午」的時刻，這裡不用算得很精準
          val nearPrevMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, MERIDIAN, loc, transConfig)!! - 1 // 記得減一
          // 接著，計算「上一個」日落時刻
          val prevSetting = riseTransImpl.getGmtTransJulDay(nearPrevMeridian, SUN, SETTING, loc, transConfig)!!

          halfDayIndex = getHourIndexOfHalfDay(prevSetting, nextRising, gmtJulDay).let {
            HourIndexOfDay(it.hourStart, it.hourEnd, it.hourIndex, dayNight)
          }
        } else {
          // 目前是白天
          dayNight = DayNight.DAY
          // 先計算「接近上一個子正的時刻」，這禮不用算得很經準
          val nearPrevMidNight = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, NADIR, loc, transConfig)!! - 1 // 記得減一
          // 接著，計算「上一個」日出時刻
          val prevRising = riseTransImpl.getGmtTransJulDay(nearPrevMidNight, SUN, RISING, loc, transConfig)!!

          halfDayIndex = getHourIndexOfHalfDay(prevRising, nextSetting, gmtJulDay).let {
            HourIndexOfDay(it.hourStart, it.hourEnd, it.hourIndex, dayNight)
          }
        }

        halfDayIndex.let {
          if (dayNight == DayNight.NIGHT) {
            HourIndexOfDay(it.hourStart, it.hourEnd, it.hourIndexAfterSunrise + 12, it.dayNight)
          } else
            it
        }

      }
    }
  }


  /**
   * @param hourIndex 「半天」的 hourIndex , 1 to 12
   */
  private data class HourIndexOfHalfDay(val hourStart: GmtJulDay, val hourEnd: GmtJulDay, val hourIndex: Int)

  private fun getHourIndexOfHalfDay(from: GmtJulDay, to: GmtJulDay, gmtJulDay: GmtJulDay): HourIndexOfHalfDay {

    require(gmtJulDay in from..to) {
      "gmtJulDay $gmtJulDay not between $from and $to"
    }

    val avgHour = (to - from) / 12.0

    return (1..11).map { i ->
      val stepFrom = from + avgHour * (i - 1)
      val stepTo = from + avgHour * i
      Triple(stepFrom, stepTo , i)
    }.firstOrNull { (stepFrom, stepTo) -> gmtJulDay >= stepFrom && gmtJulDay < stepTo }
      ?.let { (stepFrom , stepTo, index) -> HourIndexOfHalfDay(stepFrom, stepTo, index)}
      ?: HourIndexOfHalfDay(from + avgHour * 11, to, 12)
  }

}
