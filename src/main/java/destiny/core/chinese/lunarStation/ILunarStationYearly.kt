package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYear
import destiny.core.chinese.StemBranch
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.ILunarStationYearly.YearShift
import java.io.Serializable
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
