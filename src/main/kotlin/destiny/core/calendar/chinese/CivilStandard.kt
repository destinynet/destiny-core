/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.toLocalDateTime
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * 民用農曆的計算基準：哪一條子午線（時區）的「午夜」決定朔與中氣屬於哪一天。
 *
 * 民曆不是一個固定的時區，而是一條有歷史切換點的時間線；朔或中氣落在兩個基準的午夜之間時，
 * 不同基準會把它歸到不同的日子，連帶影響初一、甚至置閏。各國的置閏規則相同（無中氣置閏、冬至在十一月），
 * 差別只在這條時間線。
 */
enum class CivilStandard {

  /**
   * 紫金山天文台（GB/T 33661-2017）。1929 年以前以北京地方平時（116°25′E，UTC+7:45:40）為準，之後 UTC+8。
   * 台灣、港澳、新馬同用 UTC+8，結果相同。
   */
  CHINA {
    override fun zone(gmt: GmtJulDay): ZoneOffset = timeline(gmt, BEIJING_LMT, CHINA_SWITCHES)
  },

  /**
   * 韓國天文研究院（KASI）的음력：1912 起 UTC+9、1954-03-21 回 UTC+8:30、1961-08-10 起 UTC+9（不含夏令時間）。
   * 1912 年以前 KASI 的月表是 UTC+8，不是首爾地方平時，也不是 1908 年頒行的 UTC+8:30：
   * 1900–1911 幾個落在午夜附近的朔反推，KASI 用的時區在 +7:53:35 與 +8:10:23 之間。
   */
  KOREA {
    override fun zone(gmt: GmtJulDay): ZoneOffset = timeline(gmt, UTC8, KOREA_SWITCHES)
  },

  /**
   * 日本的旧暦：1873 年改用陽曆後沒有官方機構發布，民間一律依日本標準時（1888 起 UTC+9）推算；
   * 之前是東京地方平時（139°44′40″E，UTC+9:18:59）。1961 年之後與 [KOREA] 同一條子午線。
   */
  JAPAN {
    override fun zone(gmt: GmtJulDay): ZoneOffset = timeline(gmt, TOKYO_LMT, JAPAN_SWITCHES)
  },

  /**
   * 越南的âm lịch：北越 1967-08-08 第 121-CP 號決定把曆法的標準時由 UTC+8 改為 UTC+7，自 1968 年起依 UTC+7（1975 後全國）。
   * 1967 年底以前沿用中國曆書（[CHINA]）：1967-12-01 16:09 UTC 的朔，Hồ Ngọc Đức 的年表仍歸 12-02（UTC+8）。
   * 1985 年春節中越差一個月、1968 年春節南北越不同天，都是這條切換造成的。
   */
  VIETNAM {
    override fun zone(gmt: GmtJulDay): ZoneOffset =
      if (gmt.toLocalDateTime(ZoneOffset.UTC) < VIETNAM_SWITCH) CHINA.zone(gmt) else UTC7
  };

  /** 此瞬間在這套民曆下所用的時區 */
  abstract fun zone(gmt: GmtJulDay): ZoneOffset

  companion object {
    private val UTC7: ZoneOffset = ZoneOffset.ofHours(7)
    private val UTC8: ZoneOffset = ZoneOffset.ofHours(8)
    private val UTC830: ZoneOffset = ZoneOffset.ofHoursMinutes(8, 30)
    private val UTC9: ZoneOffset = ZoneOffset.ofHours(9)

    private val BEIJING_LMT: ZoneOffset = ZoneOffset.ofHoursMinutesSeconds(7, 45, 40)
    private val TOKYO_LMT: ZoneOffset = ZoneOffset.ofHoursMinutesSeconds(9, 18, 59)

    /** 切換點（當地時間）與之後的時區 */
    private fun switches(vararg pairs: Pair<LocalDateTime, ZoneOffset>, before: ZoneOffset): List<Pair<LocalDateTime, ZoneOffset>> {
      var prev = before
      return pairs.map { (local, offset) ->
        (local.atOffset(prev).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime() to offset).also { prev = offset }
      }
    }

    private val CHINA_SWITCHES = switches(LocalDateTime.of(1929, 1, 1, 0, 0) to UTC8, before = BEIJING_LMT)

    private val KOREA_SWITCHES = switches(
      LocalDateTime.of(1912, 1, 1, 0, 0) to UTC9,
      LocalDateTime.of(1954, 3, 21, 0, 0) to UTC830,
      LocalDateTime.of(1961, 8, 10, 0, 0) to UTC9,
      before = UTC8,
    )

    private val JAPAN_SWITCHES = switches(LocalDateTime.of(1888, 1, 1, 0, 0) to UTC9, before = TOKYO_LMT)

    /** 1968-01-01 00:00 UTC+8 */
    private val VIETNAM_SWITCH: LocalDateTime = LocalDateTime.of(1967, 12, 31, 16, 0)

    /** [switches] 是 UTC 的切換瞬間 */
    private fun timeline(gmt: GmtJulDay, before: ZoneOffset, switches: List<Pair<LocalDateTime, ZoneOffset>>): ZoneOffset {
      val utc = gmt.toLocalDateTime(ZoneOffset.UTC)
      return switches.lastOrNull { (at, _) -> utc >= at }?.second ?: before
    }
  }
}
