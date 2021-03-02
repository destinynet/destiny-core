/*
 * @author smallufo
 * @date 2004/12/20
 * @time 下午 04:59:54
 */
package destiny.core.calendar.chinese

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.tools.ChineseStringTools
import destiny.tools.ChineseStringTools.digitToChinese

import java.io.Serializable

/**
 * 農曆日期的表示法（無時辰）
 * epoch : -2636/2/15
 */
interface IChineseDateModel {
  /** 第幾輪  */
  val cycle: Int?
  /** 年干支  */
  val year: StemBranch
  /** 月  */
  val month: Int
  /** 是否是潤月  */
  val isLeapMonth: Boolean
  /** 日  */
  val day: Int

  val cycleOrZero: Int
    get() = cycle ?: 0

  /**
   * 從 cycle + 年干支 , 取得西元年份
   * 注意：只論 cycle + 年干支 , 以農曆為準 , 不考慮「西元過年」至「農曆過年」之間的 gap , 仍將其視為「西元」的前一年
   */
  val westYear: Int
    get() = -2636 + (cycleOrZero - 1) * 60 + year.index

  companion object {
    /**
     * 月份數字，轉換成中文表示
     */
    fun monthToChinese(month: Int): String {
      return when {
        month < 10 -> digitToChinese(month)
        month == 10 -> "十"
        month == 11 -> "十一"
        month == 12 -> "十二"
        else -> throw IllegalArgumentException("no such month")
      }
    }

    val lunarMonthMap = mapOf(
      1 to "㊣",
      2 to "㋁",
      3 to "㋂",
      4 to "㋃",
      5 to "㋄",
      6 to "㋅",
      7 to "㋆",
      8 to "㋇",
      9 to "㋈",
      10 to "㋉",
      11 to "㋊",
      12 to "㋋",
    )

    val dateStringMap = mapOf(
      1  to "初一",
      2  to "初二",
      3  to "初三",
      4  to "初四",
      5  to "初五",
      6  to "初六",
      7  to "初七",
      8  to "初八",
      9  to "初九",
      10 to "初十",
      11 to "十一",
      12 to "十二",
      13 to "十三",
      14 to "十四",
      15 to "十五",
      16 to "十六",
      17 to "十七",
      18 to "十八",
      19 to "十九",
      20 to "廿十",
      21 to "廿一",
      22 to "廿二",
      23 to "廿三",
      24 to "廿四",
      25 to "廿五",
      26 to "廿六",
      27 to "廿七",
      28 to "廿八",
      29 to "廿九",
      30 to "三十",
      31 to "卅一",
    )
  }
}

interface IChineseDateHourModel : IChineseDateModel {
  val hourBranch: Branch
}

data class ChineseDate(
  /** 第幾輪  */
  override val cycle: Int?,
  /** 年干支  */
  override val year: StemBranch,
  /** 月  */
  override val month: Int,
  /** 是否是潤月  */
  override val isLeapMonth: Boolean,
  /** 日  */
  override val day: Int) : IChineseDateModel, Serializable {

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


/**
 * 農曆日期＋時辰(地支)的表示法
 */
data class ChineseDateHour(val chineseDate: ChineseDate,
                           override val hourBranch: Branch) : IChineseDateModel by chineseDate, IChineseDateHourModel,
  Serializable
