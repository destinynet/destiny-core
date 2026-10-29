/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.toLocalDate
import java.time.LocalDate

/**
 * 「某個瞬間屬於哪一天」。
 * 實作要有值相等（equals／hashCode）：引擎以它當歲表的快取鍵。
 */
interface DayOf {
  fun dayOf(gmt: GmtJulDay): LocalDate
}

/** 民曆的日界：[civil] 時區的午夜 */
data class CivilDayOf(val civil: CivilStandard) : DayOf {
  override fun dayOf(gmt: GmtJulDay): LocalDate = gmt.toLocalDate(civil.zone(gmt))
}

/**
 * 歲表裡的一個農曆月。
 *
 * [firstDay]／[lastDay] 是在建表所用的日界下，初一與月底落在哪一天。
 * [MonthBoundary.INSTANT] 時 [lastDay] 是下一個朔所在的那一天（當天合朔前仍屬本月，日序數可到 31）。
 */
data class LunarMonth(
  /** 農曆年（西元紀年，正月初一換年） */
  val year: Int,
  val number: Int,
  val leap: Boolean,
  /** 本月的朔 */
  val newMoon: GmtJulDay,
  /** 下個月的朔 */
  val nextNewMoon: GmtJulDay,
  /** 初一 */
  val firstDay: LocalDate,
  /** 月底 */
  val lastDay: LocalDate,
) {
  operator fun contains(date: LocalDate): Boolean = date >= firstDay && date <= lastDay
}

/**
 * 細粒度的陰陽曆：除了 [IChineseDate] 的民曆介面（以日為單位），還能依 [LunisolarConfig]
 * 與呼叫端提供的日界 [DayOf]，回答「這個瞬間是農曆幾月幾日」。
 */
interface ILunisolarCalendar : IChineseDate {

  /** 此瞬間的農曆日期 */
  fun lunarDate(gmt: GmtJulDay, lunisolar: LunisolarConfig, dayOf: DayOf): ChineseDate

  /** 該農曆年（西元紀年）的月份，12 或 13 個，依序 */
  fun monthsOf(year: Int, lunisolar: LunisolarConfig, dayOf: DayOf): List<LunarMonth>
}
