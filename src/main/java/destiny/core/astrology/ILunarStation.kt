/**
 * Created by smallufo on 2021-03-03.
 */
package destiny.core.astrology

import destiny.core.astrology.ILunarStationYearly.YearShift
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IMidnight
import destiny.core.calendar.eightwords.IMonth
import destiny.core.calendar.eightwords.IYear
import destiny.core.chinese.Branch
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.StemBranch
import destiny.core.chinese.YearType
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.LunarStation.KEY_HOUR
import destiny.tools.converters.Domains.LunarStation.KEY_MONTH
import java.io.Serializable
import java.time.Duration
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
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
                             val yearShift: YearShift,
                             private val yearImpl: IYear,
                             val chineseDateImpl: IChineseDate,
                             val dayHourImpl: IDayHour) : ILunarStationYearly, Serializable {
  override fun getYearlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {

    val shift = when (yearShift) {
      YearShift.DEFAULT -> 15
      YearShift.METHOD2 -> 23
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

  fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation

  fun getMonthlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                        monthAlgo: IFinalMonthNumber.MonthAlgo,
                        yearImpl: ILunarStationYearly,
                        dayHourImpl: IDayHour,
                        chineseDateImpl: IChineseDate,
                        monthImpl: IMonth): LunarStation {
    val yearStation: LunarStation = yearImpl.getYearlyStation(lmt, loc)

    val monthBranch = monthImpl.getMonth(lmt, loc).branch
    val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl)
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month,
      chineseDate.leapMonth,
      monthBranch,
      chineseDate.day,
      monthAlgo
    )
    return getMonthlyStation(yearStation, monthNumber)
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
@Impl([Domain(KEY_MONTH, LunarStationMonthlyAoHead.VALUE, true)])
class LunarStationMonthlyAoHead : ILunarStationMonthly, Serializable {

  override fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  companion object {
    private fun getFirstMonth(year: Planet): LunarStation {
      return when (year) {
        SUN -> 角
        MOON -> 室
        MARS -> 星
        VENUS -> 心
        SATURN -> 胃
        MERCURY -> 牛
        JUPITER -> 參
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
@Impl([Domain(KEY_MONTH, LunarStationMonthlyAnimalExplained.VALUE, false)])
class LunarStationMonthlyAnimalExplained : ILunarStationMonthly, Serializable {

  override fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  companion object {
    private fun getFirstMonth(year: Planet): LunarStation {
      return when (year) {
        SUN -> 室
        MOON -> 星
        MARS -> 牛
        MERCURY -> 參
        JUPITER -> 心
        VENUS -> 胃
        SATURN -> 角
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

  fun getDailyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): Pair<LunarStation, Int>

  fun getDailyStation(date: ChronoLocalDate, loc: ILocation): Pair<LunarStation, Int>

}

/**
 * 查表法，按照「星期幾」實作28星宿值日
 */
class LunarStationDailyImpl(private val dayHourImpl: IDayHour,
                            private val midnightImpl: IMidnight,
                            private val julDayResolver: JulDayResolver) : ILunarStationDaily, Serializable {


  /**
   * 下個子初 與 下個子正 的差距 , 取絕對值
   */
  private fun getNextZiMidnightDuration(lmt: ChronoLocalDateTime<*>, loc: ILocation): Duration {
    val nextMidnight = midnightImpl.getNextMidnight(lmt, loc, julDayResolver)
    val nextZiStart = dayHourImpl.getLmtNextStartOf(lmt, loc, Branch.子, julDayResolver)
    return Duration.between(nextZiStart, nextMidnight).abs()
  }

  override fun getDailyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): Pair<LunarStation, Int> {

    val hourSb: Branch = dayHourImpl.getHour(lmt, loc)

    val noon = lmt.with(ChronoField.HOUR_OF_DAY, 12)
      .with(ChronoField.MINUTE_OF_HOUR, 0)
      .with(ChronoField.SECOND_OF_MINUTE, 0)
    val noonJulDay = TimeTools.getGmtJulDay(noon).toInt().let {

      if (hourSb == Branch.子) {
        if (lmt.get(ChronoField.HOUR_OF_DAY) > 12) {
          // 24 時之前
          if (dayHourImpl.changeDayAfterZi) {
            // 子初換日
            it + 1
          } else {
            // 子正換日
            getNextZiMidnightDuration(lmt, loc).toHours().let { hourDiff ->
              if (hourDiff > 12)
                it
              else
                it + 1
            }
          }
        } else {
          // 0時之後
          if (dayHourImpl.changeDayAfterZi) {
            // 子初換日
            it
          } else {
            // 子正換日
            getNextZiMidnightDuration(lmt, loc).toHours().let { hourDiff ->
              if (hourDiff > 12)
                it - 1
              else
                it
            }
          }
        }
      } else {
        // 其他時辰
        it
      }
    }

    /** 陽曆 , 西元 1993年 10月 10日 一元一將 甲子日 中午 , julDay = 2451791 , [虛] 值日 */

    val sevenYuanReminder = (noonJulDay - epoch).rem(420).let {
      if (it < 0)
        it + 420
      else
        it
    }

    val yuan = (sevenYuanReminder / 60) + 1


    val lunarStation = 虛.next(sevenYuanReminder)

    return lunarStation to yuan
  }


  override fun getDailyStation(date: ChronoLocalDate, loc: ILocation): Pair<LunarStation, Int> {


    val noon = date.atTime(LocalTime.NOON)
    val day: StemBranch = dayHourImpl.getDay(noon, loc)

    // 1 ~ 7
    val dayOfWeek = date.get(ChronoField.DAY_OF_WEEK)

    val fiveElement = BranchTools.trilogy(day.branch)
    val lunarStation = map[fiveElement]!![dayOfWeek - 1]

    val yuan = lunarStation.prev(day.getAheadOf(StemBranch.甲子)).let { head: LunarStation ->
      when (head) {
        虛 -> 1
        奎 -> 2
        畢 -> 3
        鬼 -> 4
        翼 -> 5
        氐 -> 6
        箕 -> 7
        else -> throw IllegalArgumentException("impossible")
      }
    }
    return lunarStation to yuan
  }

  companion object {
    /** 陽曆 , 西元 1993年 10月 10日 一元一將 甲子日 中午 , julDay = 2451791 , [虛] 值日 */
    const val epoch: Int = 2449271

    private val map = mapOf(
      // 星期1 ~ 星期日
      水 to listOf(畢, 翼, 箕, 奎, 鬼, 氐, 虛),
      木 to listOf(張, 尾, 壁, 井, 亢, 女, 昴),
      火 to listOf(心, 室, 參, 角, 牛, 胃, 星),
      金 to listOf(危, 觜, 軫, 斗, 婁, 柳, 房),
    )
  }
}

/**
 * 時禽
 */
interface ILunarStationHourly {

  fun getHourlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation
}

/**
 * 《禽星易見》：
 * 七曜禽星會者稀，
 * 日虛月鬼火從箕，
 * 水畢木氐金奎位，
 * 土宿還從翼上推，
 * 常將日禽尋時禽，
 * 但向禽中索取時。
 * 會者一元倒一指，
 * 不會七元七首詩。
 *
 * 一元甲子的星期天的子時是 [虛] 日鼠，
 * 二元甲子的星期天的子時是 [鬼] 金羊，
 * 三元甲子的星期天的子時是 [箕] 水豹，
 * 四元甲子的星期天的子時是 [畢] 月烏，
 * 五元甲子的星期天的子時是 [氐] 土貉，
 * 六元甲子的星期天的子時是 [奎] 木狼，
 * 七元甲子的星期天的子時是 [翼] 火蛇。
 */
@Impl([Domain(KEY_HOUR, LunarStationHourlyYuanImpl.VALUE, true)])
class LunarStationHourlyYuanImpl(private val dailyImpl: ILunarStationDaily,
                                 private val dayHourImpl: IDayHour) : ILunarStationHourly, Serializable {

  override fun getHourlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {
    val (dayStation, dayYuan) = dailyImpl.getDailyStation(lmt.toLocalDate(), loc)

    val hourBranch: Branch = dayHourImpl.getHour(lmt, loc)
    TODO("Not yet implemented")
  }

  companion object {
    const val VALUE = "YUAN"
  }
}
