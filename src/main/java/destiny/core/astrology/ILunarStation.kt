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

/**
 * 二十八星宿值年
 *
 * 《協紀辨方》二十八宿分配六十甲子：
 * 一元甲子起虛，
 * 二元甲子起奎，
 * 三元甲子起畢，
 * 四元起鬼，
 * 五元起翼，
 * 六元起氐，
 * 七元起箕，
 *
 * 凡四百二十日而週，共得甲子七次，故曰七元。
 * */
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

/** 二十八星宿 值月 */
interface ILunarStationMonthly {
  fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation

  fun getMonthlyStation(lmt: ChronoLocalDateTime<*> , loc: ILocation , yearImpl : ILunarStationYearly): LunarStation {
    val yearStation: LunarStation = yearImpl.getYearlyStation(lmt, loc)

    TODO()
  }
}


/**
 * 月禽 , 《鰲頭通書》、《參籌秘書》、《八門禽遁》
 *
 * A : 會得年禽月易求，太陽需用角為頭，太陰室宿火尋馬(火星值?)，金心土胃水騎牛，木星直年參星是，次第推求順數週。
 * B : 會得年星月易求，日角月宿火星遊，水牛木參正月起，金心土胃順行周。
 *
 * A 與 B 兩歌訣其實是同一套算法
 */
class LunarStationMonthlyAoHead : ILunarStationMonthly, Serializable {

  override fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  companion object {
    private fun getFirstMonth(year: Planet): LunarStation {
      return when (year) {
        Planet.SUN -> 角
        Planet.MOON -> 室
        Planet.MARS -> 星
        Planet.VENUS -> 心
        Planet.SATURN -> 胃
        Planet.MERCURY -> 牛
        Planet.JUPITER -> 參
        else -> throw IllegalArgumentException("No such pair")
      }
    }
  }
}

/**
 * 月禽 , 《禽星易見》、《剋擇講義》
 *
 * 「日室月星火年牛，水參木心正月求，金胃土角建寅位，年起月宿例訣頭」
 *
 * 太陽值年，正月是室。
 * 太陰值年，正月起星。
 * 火星值年，正月起牛。
 * 水星值年，正月起參。
 * 木星值年，正月起心。
 * 金星值年，正月起胃。
 * 土星值年，正月起角。
 */
class LunarStationMonthlyAnimalExplained : ILunarStationMonthly, Serializable {

  override fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  companion object {
    private fun getFirstMonth(year: Planet): LunarStation {
      return when (year) {
        Planet.SUN -> 室
        Planet.MOON -> 星
        Planet.MARS -> 牛
        Planet.MERCURY -> 參
        Planet.JUPITER -> 心
        Planet.SATURN -> 胃
        Planet.VENUS -> 角
        else -> throw IllegalArgumentException("No such pair")
      }
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
      火 to listOf(心, 室, 參, 角, 牛, 胃, 星),
      金 to listOf(危, 觜, 軫, 斗, 婁, 柳, 房),
    )
  }
}
