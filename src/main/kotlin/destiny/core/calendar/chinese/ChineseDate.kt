/*
 * @author smallufo
 * @date 2004/12/20
 * @time 下午 04:59:54
 */
package destiny.core.calendar.chinese

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
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

  /** 是否是閏月  */
  val leapMonth: Boolean

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
  override val leapMonth: Boolean,
  /** 日  */
  override val day: Int) : IChineseDateModel, Serializable

fun Int.toChineseMonthString(): String {
  return buildString {
    when (val value = this@toChineseMonthString) {
      1 -> append("正月")
      in 2..9 -> append(digitToChinese(value)).append("月")
      10 -> append("十月")
      in 11..12 -> append("十").append(digitToChinese(value - 10)).append("月")
      else -> throw IllegalArgumentException("No such month : $value")
    }
  }
}

fun Int.toChineseDayString(appendDay : Boolean = false): String {
  return buildString {
    val value = this@toChineseDayString
    when (value) {
      in 1..9 -> append("初").append(digitToChinese(value))
      10 -> append("初十")
      in 11..19 -> append("十").append(digitToChinese(value - 10))
      20 -> append("二十")
      in 21..29 -> append("廿").append(digitToChinese(value - 20))
      30 -> append("三十")
      else -> throw IllegalArgumentException("No such day : $value")
    }
    if (appendDay && value > 10)
      append("日")
  }
}

fun ChineseDate.display(appendDay: Boolean = false): String {
  return buildString {
    append(year)
    append("年")
    if (leapMonth)
      append("閏")

    append(month.toChineseMonthString())
    append(day.toChineseDayString(appendDay))
  }
}

/**
 * 農曆日期＋時辰(地支)的表示法
 */
data class ChineseDateHour(val chineseDate: ChineseDate,
                           override val hourBranch: Branch) : IChineseDateModel by chineseDate, IChineseDateHourModel,
  Serializable
