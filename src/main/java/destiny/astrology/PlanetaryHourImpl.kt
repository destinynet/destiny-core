/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.astrology

import destiny.astrology.Planet.*
import destiny.astrology.TransPoint.*
import destiny.core.DayNight
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import org.apache.commons.lang3.ArrayUtils
import java.io.Serializable
import java.time.temporal.ChronoField

/** 星期六白天起，七顆行星順序： 土、木、火、日、金、水、月 */
private val seqPlanet = arrayOf(SATURN, JUPITER, MARS, SUN, VENUS, MERCURY, MOON)

/** 日期順序 */
private val seqDay = intArrayOf(6, 7, 1, 2, 3, 4, 5)

/**
 * http://www.astrology.com.tr/planetary-hours.asp
 *
 * http://pansci.asia/archives/126644
 *
 * 晝夜、分別劃分 12等分
 */
class PlanetaryHourImpl(private val riseTransImpl: IRiseTrans) : IPlanetaryHour, Serializable {

  override fun getPlanetaryHour(gmtJulDay: Double, loc: Location): PlanetaryHour {

    val t : HourIndexOfDay = getHourIndexOfDay(gmtJulDay, loc)

    val planet = getPlanet(t.hourIndex, gmtJulDay, loc)
    return PlanetaryHour(t.hourStart, t.hourEnd, t.dayNight, planet, loc)
  } // getPlanetaryHour



  override fun getPlanetaryHours(fromGmt: Double, toGmt: Double, loc: Location): List<PlanetaryHour> {
    require(fromGmt < toGmt) {
      "fromGmt : $fromGmt larger than or equal to toGmt : $toGmt"
    }


    fun fromGmtToPlanetaryHour(gmt:Double) : PlanetaryHour {
      val r : HourIndexOfDay = getHourIndexOfDay(gmt, loc)
      val planet = getPlanet(r.hourIndex, r.hourStart, loc)
      return PlanetaryHour(r.hourStart, r.hourEnd, r.dayNight, planet, loc)
    }

    return generateSequence (fromGmtToPlanetaryHour(fromGmt)) {
      fromGmtToPlanetaryHour(it.hourEnd + (1/86400.0))
    }.takeWhile { it.hourStart < toGmt }
      .toList()

  } // getPlanetaryHours , 一段時間內的 Planetary Hours



  /**
   * @param hourIndex 整天 的 hour index , from 1 to 24
   */
  private data class HourIndexOfDay(val hourStart : Double , val hourEnd : Double , val hourIndex: Int , val dayNight: DayNight)

  private fun getHourIndexOfDay(gmtJulDay: Double, loc: Location): HourIndexOfDay {

    // TODO : 極區內可能不適用
    val nextRising = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, RISING, loc)!!
    val nextSetting = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, SETTING, loc)!!

    val halfDayIndex: HourIndexOfDay
    val dayNight: DayNight
    if (nextRising < nextSetting) {
      // 目前是黑夜
      dayNight = DayNight.NIGHT
      // 先計算「接近上一個中午」的時刻，這裡不用算得很精準
      val nearPrevMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, MERIDIAN, loc)!! - 1 // 記得減一
      // 接著，計算「上一個」日落時刻
      val prevSetting = riseTransImpl.getGmtTransJulDay(nearPrevMeridian, SUN, SETTING, loc)!!

      halfDayIndex = getHourIndexOfHalfDay(prevSetting, nextRising, gmtJulDay).let { HourIndexOfDay(it.hourStart , it.hourEnd , it.hourIndex , dayNight) }
    } else {
      // 目前是白天
      dayNight = DayNight.DAY
      // 先計算「接近上一個子正的時刻」，這禮不用算得很經準
      val nearPrevMidNight = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, NADIR, loc)!! - 1 // 記得減一
      // 接著，計算「上一個」日出時刻
      val prevRising = riseTransImpl.getGmtTransJulDay(nearPrevMidNight, SUN, RISING, loc)!!

      halfDayIndex = getHourIndexOfHalfDay(prevRising, nextSetting, gmtJulDay).let { HourIndexOfDay(it.hourStart , it.hourEnd , it.hourIndex , dayNight) }
    }

    return halfDayIndex.let {
      if (dayNight == DayNight.NIGHT) {
        HourIndexOfDay(it.hourStart , it.hourEnd , it.hourIndex+12 , it.dayNight)
      } else
        it
    }
  }


  /**
   * @param hourIndex 「半天」的 hourIndex , 1 to 12
   */
  private data class HourIndexOfHalfDay(val hourStart: Double, val hourEnd: Double, val hourIndex: Int)

  private fun getHourIndexOfHalfDay(from: Double, to: Double, gmtJulDay: Double): HourIndexOfHalfDay {

    require(gmtJulDay >= from && gmtJulDay <= to) {
      "gmtJulDay $gmtJulDay not between $from and $to"
    }

    val avgHour = (to - from) / 12.0

    // TODO : 這裡應該有更 functional 的解法！
    for (i in 1..11) {
      val stepFrom = from + avgHour * (i - 1)
      val stepTo = from + avgHour * i
      if (gmtJulDay >= stepFrom && gmtJulDay < stepTo) {
        return HourIndexOfHalfDay(stepFrom , stepTo , i)
      }
    }
    return HourIndexOfHalfDay(from + avgHour * 11, to, 12)
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
    private val logger = KotlinLogging.logger { }
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
