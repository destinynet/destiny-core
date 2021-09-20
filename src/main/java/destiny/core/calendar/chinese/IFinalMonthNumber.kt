/**
 * Created by smallufo on 2017-06-25.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo.*
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.寅
import destiny.tools.ILocaleString
import java.util.*

fun MonthAlgo.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(IFinalMonthNumber::class.java.name, locale).getString(name)
  }
}

fun MonthAlgo.toString(locale: Locale) : String {
  return this.asLocaleString().toString(locale)
}

/**
 * 將一個陰曆、或是節氣日期，轉換成「月份數字」的演算法
 */
interface IFinalMonthNumber {

  /** 月份演算法  */
  enum class MonthAlgo {
    MONTH_FIXED_THIS,   // 不論有無閏月，一律固定當作本月
    MONTH_LEAP_NEXT,    // 若閏月，一律當作下月 (全書)
    MONTH_LEAP_SPLIT15, // 若閏月，15日(含)之前當本月，之後當下月
    MONTH_SOLAR_TERMS;  // 節氣盤
  }

  companion object {

    /**
     * @param monthNum       陰曆月份
     * @param leapMonth      是否閏月
     * @param monthBranch    節氣月支
     * @param monthAlgorithm 哪種演算法 . 可能為 null ! 若是 「非節氣」的狀況下， null 的話 就直接傳回 [monthNum] 即可
     * @param days           日數
     * @return 取得最終要計算的「月份」數字
     */
    fun getFinalMonthNumber(monthNum: Int, leapMonth: Boolean, monthBranch: Branch, days: Int, monthAlgorithm: MonthAlgo?): Int {
      return if (monthAlgorithm == MONTH_SOLAR_TERMS) {
        // 節氣盤的話，直接傳回 月支 數(相對於「寅」)
        monthBranch.getAheadOf(寅) + 1 // 別忘了 +1
      } else {
        getFinalMonthNumber(monthNum, leapMonth, days, monthAlgorithm)
      }
    }

    /** 承上， 不支援節氣的計算法 */
    fun getFinalMonthNumber(monthNum: Int, leapMonth: Boolean, days: Int, monthAlgorithm: MonthAlgo?): Int {
      if (monthAlgorithm == MONTH_SOLAR_TERMS)
        throw IllegalArgumentException("$MONTH_SOLAR_TERMS not accepted in this function.")
      else {
        var finalMonthNum = monthNum // 內定為本月
        if (leapMonth) {
          // 若是閏月
          if (monthAlgorithm == MONTH_LEAP_NEXT) {
            // 且設定為「一律當下月」
            finalMonthNum = monthNum + 1
          } else if (monthAlgorithm == MONTH_LEAP_SPLIT15) {
            // 且設定為「月半切割」
            if (days > 15) {
              finalMonthNum = monthNum + 1
            }
          }
        }
        return finalMonthNum
      }
    }
  }
}
