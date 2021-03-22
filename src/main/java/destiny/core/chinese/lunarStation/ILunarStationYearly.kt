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
 * [YearShift.EPOCH_1864]
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

  val yearType : YearType

  enum class YearShift {
    DEFAULT,
    EPOCH_1864
  }

  val yearShift : YearShift

  fun getYearlyIndex(lmt: ChronoLocalDateTime<*>, loc: ILocation): YearIndex

  fun getYearly(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {
    return getYearlyIndex(lmt, loc).station
  }

}


/**
 * 二十八星宿 值年
 * @param yearType 立春 [YearType.YEAR_SOLAR] 換年 或是 陰曆初一 [YearType.YEAR_LUNAR] 換年
 * */
class LunarStationYearlyImpl(override val yearType: YearType = YearType.YEAR_SOLAR,
                             override val yearShift: YearShift = YearShift.DEFAULT,
                             private val yearImpl: IYear,
                             val chineseDateImpl: IChineseDate,
                             val dayHourImpl: IDayHour) : ILunarStationYearly, Serializable {
  override fun getYearlyIndex(lmt: ChronoLocalDateTime<*>, loc: ILocation): YearIndex {

    val epoch = when(yearShift) {
      YearShift.DEFAULT -> 1564
      YearShift.EPOCH_1864 -> 1864
    }

    val diffValue = lmt.get(ChronoField.YEAR) - epoch

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


    val value = (if (yearSb == yearSb2)
      diffValue
    else
      diffValue - 1).let {
      it % 420
    }.let {
      if (it < 0)
        it + 420
      else
        it
    }

    return YearIndex(value, epoch)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationYearlyImpl) return false

    if (yearType != other.yearType) return false
    if (yearShift != other.yearShift) return false
    if (yearImpl != other.yearImpl) return false
    if (chineseDateImpl != other.chineseDateImpl) return false
    if (dayHourImpl != other.dayHourImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = yearType.hashCode()
    result = 31 * result + yearShift.hashCode()
    result = 31 * result + yearImpl.hashCode()
    result = 31 * result + chineseDateImpl.hashCode()
    result = 31 * result + dayHourImpl.hashCode()
    return result
  }


}
