/**
 * @author smallufo
 * Created on 2006/6/30 at 下午 11:13:01
 */
package destiny.core.calendar.chinese

import destiny.core.Descriptive
import destiny.core.calendar.CalType
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDay
import destiny.core.chinese.StemBranch
import org.threeten.extra.chrono.JulianDate
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.util.*

/**
 * 從 Time(LMT) / Location 取得 ChineseDate
 */
interface IChineseDate : Descriptive {

  // =============== 陽曆轉陰曆 ===============

  /**
   * @param calType
   * @param year proleptic year , 可能 <= 0
   */
  fun getChineseDate(calType: CalType, year: Int, month: Int, day: Int): ChineseDate

  fun getChineseDate(localDate: LocalDate): ChineseDate {
    return getChineseDate(CalType.GREGORIAN, localDate.year, localDate.monthValue, localDate.dayOfMonth)
  }

  fun getChineseDate(localDate: ChronoLocalDate): ChineseDate {
    val calType = if (localDate is JulianDate) CalType.JULIAN else CalType.GREGORIAN
    return getChineseDate(calType, localDate.get(ChronoField.YEAR), localDate.get(ChronoField.MONTH_OF_YEAR),
                          localDate.get(ChronoField.DAY_OF_MONTH))
  }

  /**
   * 最完整的「陽曆轉陰曆」演算法
   * 必須另外帶入 地點、日干支紀算法、時辰劃分法、子正計算方式、是否子初換日 5個參數
   */
  fun getChineseDate(lmt: ChronoLocalDateTime<*>,
                     location: ILocation,
                     dayImpl: IDay): ChineseDate


  // =============== 陰曆轉陽曆 ===============
  fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate

  fun getYangDate(cdate: ChineseDate): ChronoLocalDate {
    return getYangDate(cdate.cycleOrZero, cdate.year, cdate.isLeapMonth, cdate.month, cdate.day)
  }


  // =============== 日期操作 ===============

  /**
   * @param days 下n日，若 n = 0 , 則傳回自己
   * 若 n = -1 , 則傳回昨天
   */
  fun plusDays(chineseDate: ChineseDate, days: Int): ChineseDate

  /**
   * 承上，往回推算
   */
  fun minusDays(chineseDate: ChineseDate, days: Int): ChineseDate {
    return plusDays(chineseDate, -days)
  }


  /** 傳回「下個月」的初一  */
  fun nextMonthStart(chineseDate: ChineseDate): ChineseDate


  /** 傳回「上個月」的初一  */
  fun prevMonthStart(chineseDate: ChineseDate): ChineseDate

  /** 列出該年所有月份(以及是否是閏月) , 可能傳回 12 or 13月 (有閏月的話)  */
  fun getMonthsOf(cycle: Int, year: StemBranch): List<Pair<Int, Boolean>> {
    val list = ArrayList<Pair<Int, Boolean>>(13)
    var date = ChineseDate(cycle, year, 1, false, 1)

    while (date.year === year) {
      list.add(Pair(date.month, date.isLeapMonth))
      date = nextMonthStart(date)
    }
    return list
  }

  /** 列出該月有幾日  */
  fun getDaysOf(cycle: Int, year: StemBranch, month: Int, leap: Boolean): Int {
    // 以當月初一開始
    val date = ChineseDate(cycle, year, month, leap, 1)
    // 推算下個月初一
    val nextMonthStart = nextMonthStart(date)
    // 再往前推算一日
    val monthEnd = minusDays(nextMonthStart, 1)
    return monthEnd.day
  }

}
