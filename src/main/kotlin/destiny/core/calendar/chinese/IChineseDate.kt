/**
 * @author smallufo
 * Created on 2006/6/30 at 下午 11:13:01
 */
package destiny.core.calendar.chinese

import destiny.core.Descriptive
import destiny.core.calendar.CalType
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.chinese.StemBranch
import destiny.tools.KotlinLogging
import org.threeten.extra.chrono.JulianDate
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField

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
  fun getChineseDate(lmt: ChronoLocalDateTime<*>, location: ILocation, dayHourImpl: IDayHour): ChineseDate

  /** 計算子時狀況 */
  fun calculateZi(lmt: ChronoLocalDateTime<*>, lmtDate: ChineseDate, nextDate: ChineseDate, prevDate: ChineseDate, nextMidnightLmt: ChronoLocalDateTime<*>, nextMidnightDay: ChineseDate, changeDayAfterZi: Boolean): ChineseDate {
    return if (changeDayAfterZi) {
      // 如果是子初換日
      if (lmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
        // 而且是 24點之前 : 那就是「下一日」
        nextDate
      } else {
        // 過了 0 點
        lmtDate
      }
    } else {
      // 子正換日 : 要計算「子正」在 24 時之前或是之後
      if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
        // 「子正」在 24時之前
        if (lmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
          // LMT 在 24時之前 (LMT1 + LMT2)
          if (lmtDate.day == nextMidnightDay.day)
            lmtDate // LMT1
          else
            nextDate // LMT2
        } else
          lmtDate // LMT3 + LMT4
      } else {
        // 「子正」在 0時 之後
        if (lmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
          // LMT 在 24時之前 (LMT1 + LMT2)
          lmtDate
        } else {
          // LMT 在 0時之後 (LMT3 + LMT4)
          if (lmtDate.day == nextMidnightDay.day) {
            prevDate // LMT3
          } else
            lmtDate // LMT4
        }

      } // 「子正」在 0時 之後
    } // 子正換日
  }



  // =============== 陰曆轉陽曆 ===============
  fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate

  fun getYangDate(cdate: ChineseDate): ChronoLocalDate {
    return getYangDate(cdate.cycleOrZero, cdate.year, cdate.leapMonth, cdate.month, cdate.day)
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
    val date = ChineseDate(cycle, year, 1, false, 1)

    return generateSequence(date) {
      val nextMonth = nextMonthStart(it)
      nextMonth
    }.take(13)
      .filter { it.year == year }
      .map { it.month to it.leapMonth }
      .toList()
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



  companion object {

    private val logger = KotlinLogging.logger { }

    /**
     * 西元年份 轉成 cycle , 例如 1984 為 cycle=78 的 甲子年
     * 甲子循環 起始於 西元前2637年 , year = -2636
     * */
    private const val EPOCH = -2637

    fun getCycleOfYear(year : Int) : Int {
      val years = year - EPOCH
      return (years / 60).let { value ->
        if (years % 60 != 0)
          value + 1
        else
          value
      }
    }
  }
}
