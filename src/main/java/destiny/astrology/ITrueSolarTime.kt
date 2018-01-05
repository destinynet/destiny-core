/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 10:10:16
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import org.slf4j.LoggerFactory
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 真太陽時計算介面 <br></br>
 * Swiss Ephemeris 實作是 TrueSolarTimeImpl
 */
interface ITrueSolarTime {

  /**
   * E : Equation of Time
   * E = LAT - LMT
   * 均時差 = 真太陽時 - LMT
   * 真太陽時 = LMT + 均時差
   */
  fun getEquationSecs(gmtJulDay: Double): Double

  fun getEquationSecs(gmtTime: ChronoLocalDateTime<*>): Double {
    val gmtJulDay = TimeTools.getGmtJulDay(gmtTime)
    return getEquationSecs(gmtJulDay)
  }

  /** 取得 LMT 時刻所對應的 真太陽時  */
  fun getTrueSolarTime(lmt: ChronoLocalDateTime<*>, location: Location): ChronoLocalDateTime<*> {
    val gmt = TimeTools.getGmtFromLmt(lmt, location)
    logger.debug("gmt = {}", gmt)
    val e = getEquationSecs(gmt)
    val pair = TimeTools.splitSecond(e)

    val gmtWithE = gmt.plus(pair.first.toLong(), ChronoUnit.SECONDS).plus(pair.second.toLong(), ChronoUnit.NANOS)
    logger.debug("gmt  = {}", gmt)
    logger.debug("gmtE = {}", gmtWithE)

    val lmtWithE = TimeTools.getLmtFromGmt(gmtWithE, location)
    logger.debug("lmtE = {}", lmtWithE)

    return TimeTools.getLongitudeTime(lmtWithE, location)
  }

  companion object {

    val logger = LoggerFactory.getLogger(ITrueSolarTime::class.java)
  }

}
