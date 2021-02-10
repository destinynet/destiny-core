/**
 * Created by smallufo on 2018-06-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import mu.KotlinLogging
import java.io.Serializable
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit


/**
 * 依據太陽在黃道帶 (Ecliptic) 的度數 (Degree) 來切割年份
 */
open class YearEclipticDegreeImpl(
  /** 換年的度數 , 通常是立春點 (315) 換年 */
  override val changeYearDegree: Double = 315.0,
  private val starPositionImpl: IStarPosition<*>) : IYear, Serializable {

  override fun getYear(gmtJulDay: Double, loc: ILocation): StemBranch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, revJulDayFunc)

    val resultStemBranch: StemBranch
    //西元 1984 年為 甲子年
    val index = if (lmt.get(ChronoField.YEAR) > 0)
      (lmt.get(ChronoField.YEAR) - 1984) % 60
    else
      (1 - lmt.get(ChronoField.YEAR) - 1984) % 60

    val gmtSecondsOffset = TimeTools.getDstSecondOffset(lmt, loc).second.toDouble()

    val gmtSecondsOffsetInt = gmtSecondsOffset.toInt()
    val gmtNanoOffset = ((gmtSecondsOffset - gmtSecondsOffsetInt) * 1000000000).toInt()

    val gmt =
      lmt.minus(gmtSecondsOffsetInt.toLong(), ChronoUnit.SECONDS).minus(gmtNanoOffset.toLong(), ChronoUnit.NANOS)


    val solarLongitude = starPositionImpl.getPosition(Planet.SUN, gmt, Centric.GEO, Coordinate.ECLIPTIC).lng
    if (solarLongitude < 180)
    //立春(0)過後，到秋分之間(180)，確定不會換年
      resultStemBranch = StemBranch[index]
    else {
      // 360 > solarLongitude >= 180

      //取得 lmt 當年 1/1 凌晨零分的度數
      val startOfYear = lmt
        .with(ChronoField.DAY_OF_YEAR, 1)
        .with(ChronoField.HOUR_OF_DAY, 0)
        .with(ChronoField.MINUTE_OF_HOUR, 0)
        .minus(gmtSecondsOffsetInt.toLong(), ChronoUnit.SECONDS)

      val degreeOfStartOfYear =
        starPositionImpl.getPosition(Planet.SUN, startOfYear, Centric.GEO, Coordinate.ECLIPTIC).lng

      if (changeYearDegree >= degreeOfStartOfYear) {
        resultStemBranch = if (solarLongitude >= changeYearDegree)
          StemBranch[index]
        else if (changeYearDegree > solarLongitude && solarLongitude >= degreeOfStartOfYear) {
          val tempTime = gmt.minus((180 * 24 * 60 * 60).toLong(), ChronoUnit.SECONDS)
          if (TimeTools.isBefore(tempTime, startOfYear))
            StemBranch[index - 1]
          else
            StemBranch[index]
        } else
          StemBranch[index]
      } else {
        // degreeOfStartOfYear > changeYearDegree >= 秋分 (180)
        resultStemBranch = if (solarLongitude >= degreeOfStartOfYear) {
          val tempTime = gmt.minus((180 * 24 * 60 * 60).toLong(), ChronoUnit.SECONDS)
          if (TimeTools.isBefore(tempTime, startOfYear))
            StemBranch[index]
          else
            StemBranch[index + 1]
        } else {
          if (solarLongitude >= changeYearDegree)
            StemBranch[index + 1]
          else
            StemBranch[index]
        }
      }
    }
    return resultStemBranch
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is YearEclipticDegreeImpl) return false

    if (changeYearDegree != other.changeYearDegree) return false

    return true
  }

  override fun hashCode(): Int {
    return changeYearDegree.hashCode()
  }


  companion object {
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
    val logger = KotlinLogging.logger { }
  }
}
