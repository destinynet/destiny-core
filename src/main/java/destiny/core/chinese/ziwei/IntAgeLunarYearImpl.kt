/**
 * Created by smallufo on 2022-06-04.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.astrology.IRelativeTransit
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.AgeType
import destiny.core.chinese.StemBranch
import java.time.LocalTime


/**
 * 陰曆計算歲數 , 大年初一日月合朔切換歲數 , 通常用於紫微盤
 * @param ageType 虛歲實歲完整相差一歲
 */
class IntAgeLunarYearImpl(private val ageType: AgeType,
                          private val chineseDateImpl: IChineseDate,
                          private val relativeTransitImpl: IRelativeTransit,
                          private val julDayResolver: JulDayResolver) : IIntAge, java.io.Serializable {

  override fun getRange(gender: Gender, gmtJulDay: GmtJulDay, loc: ILocation, age: Int): Pair<GmtJulDay, GmtJulDay> {
    if (ageType == AgeType.VIRTUAL && age == 0)
      throw IllegalArgumentException("VirtualAge doesn't support age = 0")
    val initAge = Pair(gmtJulDay, getNextYearSunMoonConj(gmtJulDay))
    return getRangeInner(initAge, age)
  }

  override fun getRanges(gender: Gender, gmtJulDay: GmtJulDay, loc: ILocation, fromAge: Int, toAge: Int): List<Pair<GmtJulDay, GmtJulDay>> {
    require(fromAge <= toAge) {
      ("fromAge must be <= toAge")
    }
    val from = getRange(gender, gmtJulDay, loc, fromAge)
    val result = ArrayList<Pair<GmtJulDay, GmtJulDay>>(toAge - fromAge + 1)
    result.add(from)
    return getRangesInner(result, toAge - fromAge)
  }

  private fun getRangeInner(prevResult: Pair<GmtJulDay, GmtJulDay>, count: Int): Pair<GmtJulDay, GmtJulDay> {

    val startAge = when(ageType) {
      AgeType.REAL -> 0
      AgeType.VIRTUAL -> 1
    }

    return if (count == startAge) {
      prevResult
    } else {
      val newStart = prevResult.second
      val newEnd = getNextYearSunMoonConj(prevResult.second + 2)
      getRangeInner(Pair(newStart, newEnd), count - 1)
    }
  }

  private fun getRangesInner(prevResults: MutableList<Pair<GmtJulDay, GmtJulDay>>, count: Int): List<Pair<GmtJulDay, GmtJulDay>> {
    val startAge = when (ageType) {
      AgeType.REAL    -> 0
      AgeType.VIRTUAL -> 1
    }

    return if (count == startAge) {
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

  private fun getNextYearSunMoonConj(gmtJulDay: GmtJulDay): GmtJulDay {
    val dateTime = julDayResolver.getLocalDateTime(gmtJulDay)

    // 陰曆日期
    val chineseDate = chineseDateImpl.getChineseDate(dateTime.toLocalDate())

    val next1Year = getNextYear(chineseDate.cycleOrZero, chineseDate.year)
    val next1YearJan2 = ChineseDate(next1Year.first, next1Year.second, 1, false, 2)
    // 利用「隔年、陰曆、一月二日、中午」作為「逆推」日月合朔的時間點
    val next1YearJan2Time = chineseDateImpl.getYangDate(next1YearJan2).atTime(LocalTime.NOON)

    val next1YearJan2Gmt = TimeTools.getGmtJulDay(next1YearJan2Time)

    return relativeTransitImpl.getRelativeTransit(Planet.MOON, Planet.SUN, 0.0, next1YearJan2Gmt, false)
      ?: throw RuntimeException("Cannot get Sun/Moon Conj since julDay = $next1YearJan2Gmt")
  }


  private fun getNextYear(cycle: Int, year: StemBranch): Pair<Int, StemBranch> {
    return if (year.index == 59) {
      // 癸亥
      Pair(cycle + 1, year.next(1))
    } else {
      Pair(cycle, year.next(1))
    }
  }
}
