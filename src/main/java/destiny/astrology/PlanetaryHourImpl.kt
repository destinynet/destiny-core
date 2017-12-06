/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.astrology

import destiny.astrology.Planet.*
import destiny.astrology.TransPoint.*
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import org.apache.commons.lang3.ArrayUtils
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple3
import org.jooq.lambda.tuple.Tuple4
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.function.Function

/**
 *
 *
 * http://www.astrology.com.tr/planetary-hours.asp
 *
 *
 * http://pansci.asia/archives/126644
 *
 *
 * 晝夜、分別劃分 12等分
 */
class PlanetaryHourImpl(private val riseTransImpl: IRiseTrans) : IPlanetaryHour, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun getPlanetaryHour(gmtJulDay: Double, loc: Location): PlanetaryHour {

    // hourStart , hourEnd , hourIndex (1 to 24)
    val tuples = getHourIndexOfDay(gmtJulDay, loc)

    val planet = getPlanet(tuples.v3(), gmtJulDay, loc)
    return PlanetaryHour(tuples.v1(), tuples.v2(), tuples.v4(), planet, loc)
  } // getPlanetaryHour


  override fun getPlanetaryHours(fromGmt: Double, toGmt: Double, loc: Location): List<PlanetaryHour> {
    if (fromGmt >= toGmt) {
      throw RuntimeException("fromGmt : $fromGmt larger than or equal to toGmt : $toGmt")
    }

    val result = ArrayList<PlanetaryHour>()

    var cursor = fromGmt
    while (cursor < toGmt) {
      // hourStart , hourEnd , hourIndex (1 to 24)
      val hourIndexOfDayAndAvgHour = getHourIndexOfDay(cursor, loc)
      val hourStart = hourIndexOfDayAndAvgHour.v1()
      val hourEnd = hourIndexOfDayAndAvgHour.v2()
      val dayIndex = hourIndexOfDayAndAvgHour.v3()
      val dayNight = hourIndexOfDayAndAvgHour.v4()
      val planet = getPlanet(dayIndex, hourStart, loc)

      val planetaryHour = PlanetaryHour(hourStart, hourEnd, dayNight, planet, loc)
      result.add(planetaryHour)

      cursor = hourEnd + 1 / 86400.0
    }
    return result
  } // getPlanetaryHours , 一段時間內的 Planetary Hours


  /**
   * tuple1 : hourStart
   * tuple2 : hourEnd
   * tuple3 : 整天 的 hour index , from 1 to 24
   * tuple4 : DayNight
   */
  private fun getHourIndexOfDay(gmtJulDay: Double, loc: Location): Tuple4<Double, Double, Int, DayNight> {

    val nextRising = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, RISING, loc)
    val nextSetting = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, SETTING, loc)

    val halfDayIndex: Tuple4<Double, Double, Int, DayNight>
    val dayNight: DayNight
    if (nextRising < nextSetting) {
      // 目前是黑夜
      dayNight = DayNight.NIGHT
      // 先計算「接近上一個中午」的時刻，這裡不用算得很精準
      val nearPrevMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, MERIDIAN, loc) - 1 // 記得減一
      // 接著，計算「上一個」日落時刻
      val prevSetting = riseTransImpl.getGmtTransJulDay(nearPrevMeridian, SUN, SETTING, loc)

      halfDayIndex = getHourIndexOfHalfDay(prevSetting, nextRising, gmtJulDay).concat(dayNight)
    } else {
      // 目前是白天
      dayNight = DayNight.DAY
      // 先計算「接近上一個子正的時刻」，這禮不用算得很經準
      val nearPrevMidNight = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, NADIR, loc) - 1 // 記得減一
      // 接著，計算「上一個」日出時刻
      val prevRising = riseTransImpl.getGmtTransJulDay(nearPrevMidNight, SUN, RISING, loc)

      halfDayIndex = getHourIndexOfHalfDay(prevRising, nextSetting, gmtJulDay).concat(dayNight)
    }

    return halfDayIndex.map3 { value -> if (dayNight == DayNight.NIGHT) value!! + 12 else value } // 夜晚 + 12
  }

  /**
   * 「半天」的 hour index , from 1 to 12
   * @return Tuple[ hourStart , hourEnd , hourIndex]
   */
  private fun getHourIndexOfHalfDay(from: Double, to: Double, gmtJulDay: Double): Tuple3<Double, Double, Int> {
    if (gmtJulDay < from || gmtJulDay > to) {
      // gmtJulDay 一定要在 from 與 to 的範圍內
      throw RuntimeException("gmtJulDay $gmtJulDay not between $from and $to")
    } else {
      val avgHour = (to - from) / 12.0
      for (i in 1..11) {
        val stepFrom = from + avgHour * (i - 1)
        val stepTo = from + avgHour * i
        if (gmtJulDay >= stepFrom && gmtJulDay < stepTo) {
          return Tuple.tuple(stepFrom, stepTo, i)
        }
      }
      return Tuple.tuple(from + avgHour * 11, to, 12)
    }
  } // getHourIndexOfHalfDay , return 1 to 12

  private fun getPlanet(hourIndexOfDay: Int, gmtJulDay: Double, loc: Location): Planet {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, revJulDayFunc)

    // 1:星期一 , 2:星期二 ... , 6:星期六 , 7:星期日
    val dayOfWeek = lmt.get(ChronoField.DAY_OF_WEEK)

    logger.debug("dayOfWeek = {}", dayOfWeek)

    // from 0 to 6
    val indexOfDayTable = ArrayUtils.indexOf(seqDay, dayOfWeek)
    logger.debug("indexOfDayTable = {}", indexOfDayTable)

    // 0 to (24x7-1)
    val hourIndexFromSaturday = indexOfDayTable * 24 + hourIndexOfDay - 1
    logger.debug("hourIndexFromSaturday = {}", hourIndexFromSaturday)

    return seqPlanet[hourIndexFromSaturday % 7]
  }

  companion object {

    /**
     * 星期六白天起，七顆行星順序： 土、木、火、日、金、水、月
     */
    private val seqPlanet = arrayOf(SATURN, JUPITER, MARS, SUN, VENUS, MERCURY, MOON)

    /**
     * 日期順序
     */
    private val seqDay = intArrayOf(6, 7, 1, 2, 3, 4, 5)

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
