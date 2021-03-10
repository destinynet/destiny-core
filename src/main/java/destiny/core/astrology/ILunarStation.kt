/**
 * Created by smallufo on 2021-03-03.
 */
package destiny.core.astrology

import destiny.core.astrology.ILunarStationYearly.YearShift
import destiny.core.astrology.LunarStation.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.eightwords.IDay
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYear
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.StemBranch
import destiny.core.chinese.YearType
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.LunarStation.KEY_MONTH
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField

/**
 * 二十八星宿值年
 *
 *
 * [YearShift.METHOD2]
 * 年禽還有一種推法，基本定位為
 * 公元964年為六元甲子，
 * 公元1144年為七元甲子，
 * 公元1324年為一元甲子，
 * 公元1504年為二元上元甲子，
 * 1684年為三元上元甲子，
 * 1864年為四元上元甲子，
 * 按此推法，
 * 則1864年為甲子虛，
 * 1924年為中元甲子奎，
 * 1984年為下元甲子畢。
 * 按二十八宿次序順推即可。按此法則
 * 2008年為奎木狼，
 * 2009年為婁金狗，
 * 2010年為胃土雉，
 * 依次類推。
 * */
interface ILunarStationYearly {

  enum class YearShift {
    DEFAULT,
    METHOD2
  }

  fun getYearlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation
}


/**
 * 二十八星宿 值年
 * @param yearType 立春 [YearType.YEAR_SOLAR] 換年 或是 陰曆初一 [YearType.YEAR_LUNAR] 換年
 * */
class LunarStationYearlyImpl(val yearType: YearType,
                             val yearShift: ILunarStationYearly.YearShift,
                             private val yearImpl: IYear,
                             val chineseDateImpl: IChineseDate,
                             val dayHourImpl: IDayHour) : ILunarStationYearly, Serializable {
  override fun getYearlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {

    val shift = when (yearShift) {
      ILunarStationYearly.YearShift.DEFAULT -> 15
      ILunarStationYearly.YearShift.METHOD2 -> 23
    }

    val index = ((lmt.get(ChronoField.YEAR) + shift) % 28).let { r ->
      if (r == 0)
        27
      else
        r - 1
    }

    val (yearSb, yearSb2) = if (yearType == YearType.YEAR_SOLAR) {
      // 節氣立春換年
      val yearSb: StemBranch = yearImpl.getYear(lmt, loc)
      // 以七月再算一次 年干支
      val yearSb2: StemBranch = yearImpl.getYear(lmt.with(ChronoField.MONTH_OF_YEAR, 7), loc)
      yearSb to yearSb2
    } else {
      // 陰曆初一換年
      val yearSb = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl).year
      // 以七月再算一次 年干支
      val yearSb2: StemBranch =
        chineseDateImpl.getChineseDate(lmt.with(ChronoField.MONTH_OF_YEAR, 7), loc, dayHourImpl).year
      yearSb to yearSb2
    }

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

  enum class LunarType {
    /**
     * 《鰲頭通書》、《參籌秘書》、《八門禽遁》
     */
    AO_HEAD,

    /**
     * 月禽 , 《禽星易見》、《剋擇講義》
     * 「日室月星火年牛，水參木心正月求，金胃土角建寅位，年起月宿例訣頭」
     */
    ANIMAL_EXPLAINED
  }

  fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation

  fun getMonthlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation, yearImpl: ILunarStationYearly): LunarStation {
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
@Impl([Domain(KEY_MONTH , LunarStationMonthlyAoHead.VALUE , true)])
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
    const val VALUE = "AO_HEAD"
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
@Impl([Domain(KEY_MONTH , LunarStationMonthlyAnimalExplained.VALUE , false)])
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
    const val VALUE = "ANIMAL_EXPLAINED"
  }
}

/**
 * 二十八星宿值日
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
 *
 * */
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
