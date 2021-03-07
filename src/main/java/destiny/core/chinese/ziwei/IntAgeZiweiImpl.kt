/**
 * Created by smallufo on 2017-10-21.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.astrology.IRelativeTransit
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.LocalTime
import java.util.*

/**
 * 紫微斗數虛歲
 * 出生當下，即為一歲。（故， age 不可以 <= 0）
 * 「一歲」終止於「順推」的「陰曆一月一日」
 */
class IntAgeZiweiImpl(private val chineseDateImpl: IChineseDate,
                      private val relativeTransitImpl: IRelativeTransit,
                      private val julDayResolver: JulDayResolver) : IIntAge, Serializable {



  override fun getRange(gender: Gender, gmtJulDay: Double, loc: ILocation, age: Int): Pair<Double, Double> {

    val age1 = Pair(gmtJulDay, getNextYearSunMoonConj(gmtJulDay))
    return getRangeInner(age1, age)
  }

  private fun getRangeInner(prevResult: Pair<Double, Double>, count: Int): Pair<Double, Double> {
    return if (count == 1) {
      prevResult
    } else {
      val newStart = prevResult.second
      val newEnd = getNextYearSunMoonConj(prevResult.second + 2)
      getRangeInner(Pair(newStart, newEnd), count - 1)
    }
  }


  private fun getNextYearSunMoonConj(gmtJulDay: Double): Double {
    val dateTime = julDayResolver.getLocalDateTime(gmtJulDay)

    // 陰曆日期
    val chineseDate = chineseDateImpl.getChineseDate(dateTime.toLocalDate())

    val next1Year = getNextYear(chineseDate.cycleOrZero, chineseDate.year)
    val next1YearJan2 = ChineseDate(next1Year.first, next1Year.second, 1, false, 2)
    // 利用「隔年、陰曆、一月二日、中午」作為「逆推」日月合朔的時間點
    val next1YearJan2Time = chineseDateImpl.getYangDate(next1YearJan2).atTime(LocalTime.NOON)

    val next1YearJan2Gmt = TimeTools.getGmtJulDay(next1YearJan2Time)
    val value = relativeTransitImpl.getRelativeTransit(Planet.MOON, Planet.SUN, 0.0, next1YearJan2Gmt, false)
    if (value != null)
      return value
    else
      throw RuntimeException("Cannot get Sun/Moon Conj since julDay = $next1YearJan2Gmt")
  }


  private fun getNextYear(cycle: Int, year: StemBranch): Pair<Int, StemBranch> {
    return if (year.index == 59) {
      // 癸亥
      Pair(cycle + 1, year.next(1))
    } else {
      Pair(cycle, year.next(1))
    }
  }

  override fun getRanges(gender: Gender, gmtJulDay: Double, loc: ILocation, fromAge: Int, toAge: Int): List<Pair<Double, Double>> {
    if (fromAge > toAge) {
      throw RuntimeException("fromAge must be <= toAge")
    }
    val from = getRange(gender, gmtJulDay, loc, fromAge)
    val result = ArrayList<Pair<Double, Double>>(toAge - fromAge + 1)
    result.add(from)
    return getRangesInner(result, toAge - fromAge)
  }

  private fun getRangesInner(prevResults: MutableList<Pair<Double, Double>>, count: Int): List<Pair<Double, Double>> {
    return if (count == 0) {
      prevResults
    } else {
      val (_, second) = prevResults[prevResults.size - 1]
      val stepDay = second + 1

      val end = getNextYearSunMoonConj(stepDay)
      val newResult = Pair(second, end)
      prevResults.add(newResult)
      getRangesInner(prevResults, count - 1)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is IntAgeZiweiImpl) return false

    if (relativeTransitImpl != other.relativeTransitImpl) return false

    return true
  }

  override fun hashCode(): Int {
    return relativeTransitImpl.hashCode()
  }

}
