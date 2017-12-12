/*
 * @author smallufo
 * @date 2004/12/20
 * @time 下午 04:59:54
 */
package destiny.core.calendar.chinese

import destiny.core.chinese.StemBranch
import destiny.tools.ChineseStringTools

import java.io.Serializable

/**
 * 農曆日期的表示法（無時辰）
 * epoch : -2636/2/15
 */
open class ChineseDate(
  /** 第幾輪  */
  private val cycle: Int?,
  /** 年干支  */
  val year: StemBranch,
  /** 月  */
  val month: Int,
  /** 是否是潤月  */
  val isLeapMonth: Boolean,
  /** 日  */
  val day: Int) : Serializable {

  val cycleOrZero: Int
    get() = cycle ?: 0

  /**
   * 從 cycle + 年干支 , 取得西元年份
   * 注意：只論 cycle + 年干支 , 以農曆為準 , 不考慮「西元過年」至「農曆過年」之間的 gap , 仍將其視為「西元」的前一年
   */
  val westYear: Int
    get() = -2636 + (cycleOrZero - 1) * 60 + year.index


  override fun toString(): String {
    return year.toString() + "年" + (if (isLeapMonth) "閏" else "") + toChinese(month) + "月" + toChinese(day) + "日"
  }

  companion object {

    fun toChinese(num: Int): String {
      return when {
        num < 10 -> digitToChinese(num)
        num == 10 -> "十"
        num in 11..19 -> "十" + digitToChinese(num - 10)
        num == 20 -> "二十"
        num in 21..29 -> "二十" + digitToChinese(num - 20)
        else -> "三十"
      }
    }

    private fun digitToChinese(digit: Int): String {
      return ChineseStringTools.digitToChinese(digit)
    }
  }
}
