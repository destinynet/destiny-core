/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.eightwords.DayHourConfig
import kotlinx.serialization.Serializable

/** 月界：合朔「那一天」換月，還是合朔「那一刻」換月 */
@Serializable
enum class MonthBoundary {
  /** 曆法定義：合朔那一天整天是初一；日序數最多 30 */
  DAY,

  /**
   * 合朔那一刻換月：同一天在合朔前屬上個月（日序數可到 31）、合朔後是下個月初一。
   * 月界與地點無關；只有「第幾日」要靠 [DayBoundary]。
   */
  INSTANT
}

/** 月表從哪裡來：保留多少萬年曆 */
@Serializable
enum class MonthTable {
  /**
   * 萬年曆（[CivilStandard]）的月名與初一日期都照用；[DayBoundary] 只決定一個瞬間屬於哪個民曆日期。
   * 八字、紫微排盤一直以來的做法：子時依換日法歸到前一天或後一天，再查那一天的農曆。
   */
  CIVIL,

  /**
   * 月名（含置閏）照萬年曆；初一是「含合朔那一刻的那一天」，一天從何時起依本設定的日界。
   * 合朔落在換日點附近時，整個月的日期與萬年曆差一天。
   */
  CIVIL_NAMES,

  /**
   * 連置閏都依本設定的日界／月界重算。三百年裡約有一成的年份月名與萬年曆不同
   * （中氣與朔同一天、中氣在前時，兩種算法把它歸到不同月）。
   */
  DERIVED
}

/** 日界：「某個瞬間屬於哪一天」 */
@Serializable
enum class DayBoundary {
  /** 依八字日柱的設定：時辰切割（真太陽時／平太陽時）× 換日點（子初／子正），只看經度 */
  DAY_HOUR,

  /** 民曆的做法：[CivilStandard] 時區的午夜，與地點無關 */
  CIVIL
}

/**
 * 陰陽曆的細部規則。預設值就是現行行為：民曆的月表，日界依八字日柱設定、只決定子時歸哪一天。
 *
 * - 萬年曆：`LunisolarConfig(dayBoundary = DayBoundary.CIVIL)`
 * - 初一隨當地日界移動、月名照萬年曆：`LunisolarConfig(monthTable = MonthTable.CIVIL_NAMES)`
 * - 合朔那一刻換月、月名照萬年曆（紫雲派）：`LunisolarConfig(monthBoundary = MonthBoundary.INSTANT)`
 * - 全部依當地日界重算（研究用）：`LunisolarConfig(monthTable = MonthTable.DERIVED)`
 */
@Serializable
data class LunisolarConfig(
  val civil: CivilStandard = CivilStandard.CHINA,
  val dayBoundary: DayBoundary = DayBoundary.DAY_HOUR,
  val monthBoundary: MonthBoundary = MonthBoundary.DAY,
  val monthTable: MonthTable = MonthTable.CIVIL,
)

/** [ChineseDateFeature] 的設定：陰陽曆規則＋日界用的八字日柱設定 */
@Serializable
data class ChineseDateConfig(
  val lunisolar: LunisolarConfig = LunisolarConfig(),
  val dayHourConfig: DayHourConfig = DayHourConfig(),
)
