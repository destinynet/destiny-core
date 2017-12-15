/**
 * Created by smallufo on 2017-10-21.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.IRelativeTransit
import destiny.astrology.Planet
import destiny.core.Gender
import destiny.core.IntAge
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.ChineseDateIF
import destiny.core.chinese.StemBranch
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.LocalTime
import java.util.*


/**
 * 紫微斗數虛歲
 * 出生當下，即為一歲。（故， age 不可以 <= 0）
 * 「一歲」終止於「順推」的「陰曆一月一日」
 */
class IntAgeZiweiImpl(private val chineseDateImpl: ChineseDateIF, private val relativeTransitImpl: IRelativeTransit) : IntAge, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun getRange(gender: Gender, gmtJulDay: Double, loc: Location, age: Int): Pair<Double, Double> {

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
    val dateTime = revJulDayFunc.invoke(gmtJulDay)

    // 陰曆日期
    val chineseDate = chineseDateImpl.getChineseDate(dateTime.toLocalDate())

    val next1Year = getNextYear(chineseDate.cycleOrZero, chineseDate.year)
    val next1YearJan2 = ChineseDate(next1Year.v1(), next1Year.v2(), 1, false, 2)
    // 利用「隔年、陰曆、一月二日、中午」作為「逆推」日月合朔的時間點
    val next1YearJan2Time = chineseDateImpl.getYangDate(next1YearJan2).atTime(LocalTime.NOON)

    val next1YearJan2Gmt = TimeTools.getGmtJulDay(next1YearJan2Time)
    val value = relativeTransitImpl.getRelativeTransit(Planet.MOON, Planet.SUN, 0.0, next1YearJan2Gmt, false)
    if (value != null)
      return value
    else
      throw RuntimeException("Cannot get Sun/Moon Conj since julDay = " + next1YearJan2Gmt)
  }


  private fun getNextYear(cycle: Int, year: StemBranch): Tuple2<Int, StemBranch> {
    return if (year.index == 59) {
      // 癸亥
      Tuple.tuple(cycle + 1, year.next(1))
    } else {
      Tuple.tuple(cycle, year.next(1))
    }
  }

  override fun getRanges(gender: Gender, gmtJulDay: Double, loc: Location, fromAge: Int, toAge: Int): List<Pair<Double, Double>> {
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

  companion object {
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
