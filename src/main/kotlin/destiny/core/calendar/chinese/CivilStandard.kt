/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.toLocalDate
import java.time.ZoneOffset

/**
 * 民用農曆的計算基準：哪一條子午線（時區）的「午夜」決定朔與中氣屬於哪一天。
 *
 * 民曆不是一個固定的時區，而是一條有歷史切換點的時間線；朔或中氣落在兩個基準的午夜之間時，
 * 不同基準會把它歸到不同的日子，連帶影響初一、甚至置閏。
 */
enum class CivilStandard {

  /**
   * 紫金山天文台（GB/T 33661-2017）。1929 年以前以北京地方平時（116°25′E，UTC+7:45:40）為準，之後 UTC+8。
   * 台灣、港澳、新馬同用 UTC+8，結果相同。
   */
  CHINA {
    override fun zone(gmt: GmtJulDay): ZoneOffset =
      if (gmt.toLocalDate(UTC8).year < 1929) BEIJING_LMT else UTC8
  };

  /** 此瞬間在這套民曆下所用的時區 */
  abstract fun zone(gmt: GmtJulDay): ZoneOffset

  companion object {
    private val UTC8: ZoneOffset = ZoneOffset.ofHours(8)
    private val BEIJING_LMT: ZoneOffset = ZoneOffset.ofHoursMinutesSeconds(7, 45, 40)
  }
}
