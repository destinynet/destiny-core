/**
 * Created by smallufo on 2021-03-03.
 */
package destiny.core.astrology

import destiny.core.astrology.LunarStation.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.eightwords.IDay
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYear
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField

/** 二十八星宿值年 */
interface ILunarStationYearly {
  fun getYearlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation
}


/** 二十八星宿 值年 , 節氣 劃分 */
class LunarStationYearlyBySolarTerms(private val yearImpl: IYear) : ILunarStationYearly, Serializable {
  override fun getYearlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {
    val index = ((lmt.get(ChronoField.YEAR) + 15) % 28).let { r ->
      if (r == 0)
        27
      else
        r - 1
    }
    val yearSb: StemBranch = yearImpl.getYear(lmt, loc)
    // 以七月再算一次 年干支
    val yearSb2: StemBranch = yearImpl.getYear(lmt.with(ChronoField.MONTH_OF_YEAR, 7), loc)
    return LunarStation.values[index].let {
      if (yearSb == yearSb2)
        it
      else
        it.prev
    }
  }
}

/** 二十八星宿 值年 , 農曆 劃分 */
class LunarStationYearlyByLunarYear(val chineseDateImpl: IChineseDate,
                                    val dayHourImpl: IDayHour) : ILunarStationYearly, Serializable {
  override fun getYearlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {
    val index = ((lmt.get(ChronoField.YEAR) + 15) % 28).let { r ->
      if (r == 0)
        27
      else
        r - 1
    }
    val yearSb = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl).year
    // 以七月再算一次 年干支
    val yearSb2: StemBranch =
      chineseDateImpl.getChineseDate(lmt.with(ChronoField.MONTH_OF_YEAR, 7), loc, dayHourImpl).year

    return LunarStation.values[index].let {
      if (yearSb == yearSb2)
        it
      else
        it.prev
    }
  }
}

/** 二十八星宿值日 */
interface ILunarStationDaily {

  fun getDailyStation(date: LocalDate, loc: ILocation): LunarStation
}

/**
 * 查表法，按照「星期幾」實作28星宿值日
 */
class LunarStationDailyByWeek(private val dayImpl: IDay) : ILunarStationDaily, Serializable {

  override fun getDailyStation(date: LocalDate, loc: ILocation): LunarStation {
    val noon = date.atTime(12, 0)
    val day: StemBranch = dayImpl.getDay(noon, loc)
    val week: DayOfWeek = date.dayOfWeek

    val fiveElement = BranchTools.trilogy(day.branch)
    return map[fiveElement]!![week.value - 1]
  }

  companion object {
    private val map = mapOf(
      // 星期1 ~ 星期日
      水 to listOf(畢, 翼, 箕, 奎, 鬼, 氐, 虛),
      木 to listOf(張, 尾, 壁, 井, 亢, 女, 昴),
      火 to listOf(心, 室, 参, 角, 牛, 胃, 星),
      金 to listOf(危, 觜, 軫, 斗, 婁, 柳, 房),
    )
  }
}
