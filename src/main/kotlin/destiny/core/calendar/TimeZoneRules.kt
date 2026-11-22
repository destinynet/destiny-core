/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

/**
 * 時區規則的**接縫**：destiny-core 裡所有「查時區資料庫」的動作都經過這裡。
 *
 * 對外只用平台中立的型別（tzid 字串、[kotlin.time.Instant]、kotlinx [LocalDateTime]）。
 * 目前以 `java.time.zone.ZoneRules` 實作，是 core 裡唯一直接碰它的檔案（KMP quarantine 上刻意保留）；
 * 轉 KMP 時改成 `expect`／`actual` —— kotlinx-datetime 沒有「標準時差」「夏令時間時數」「下一次切換」這些查詢，
 * 非 JVM 平台要另外實作或帶時區資料。
 *
 * 時區資料庫的版本跟著執行環境（JVM 的 tzdb）。歷史時刻（1900 年前後的地方平時、戰時夏令時間）
 * 在不同版本間可能不同 —— 由 core-impl 的 `TimeGoldenMasterTest` 把關。
 */
object TimeZoneRules {

  private fun rules(tzid: String) = ZoneId.of(tzid).rules

  /** 在 [at] 這一刻與 GMT 的時差（秒，含夏令時間） */
  fun offsetSeconds(tzid: String, at: Instant): Int =
    rules(tzid).getOffset(at.toJavaInstant()).totalSeconds

  /** 在 [at] 這一刻的「標準時差」（秒，不含夏令時間） */
  fun standardOffsetSeconds(tzid: String, at: Instant): Int =
    rules(tzid).getStandardOffset(at.toJavaInstant()).totalSeconds

  /** 在 [at] 這一刻夏令時間多撥了幾秒（沒有夏令時間 ＝ 0） */
  fun daylightSavingSeconds(tzid: String, at: Instant): Int =
    rules(tzid).getDaylightSavings(at.toJavaInstant()).seconds.toInt()

  fun isDaylightSaving(tzid: String, at: Instant): Boolean =
    rules(tzid).isDaylightSavings(at.toJavaInstant())

  /** [after] 之後的下一次時差切換；沒有就 null */
  fun nextTransition(tzid: String, after: Instant): Instant? =
    rules(tzid).nextTransition(after.toJavaInstant())?.instant?.toKotlinInstant()

  /** 現在與 GMT 時差為 [offsetSeconds] 的所有 tzid（依時區資料庫的順序） */
  fun tzidsByOffsetSeconds(offsetSeconds: Int, at: Instant = kotlin.time.Clock.System.now()): List<String> {
    val offset = ZoneOffset.ofTotalSeconds(offsetSeconds)
    val javaAt = at.toJavaInstant()
    return ZoneId.getAvailableZoneIds().filter { ZoneId.of(it).rules.getOffset(javaAt) == offset }
  }

  /** 承上，挑一個代表：有 `Etc/` 開頭的先用，否則取最短的；沒有就 null */
  fun tzidByOffsetSeconds(offsetSeconds: Int): String? {
    val list = tzidsByOffsetSeconds(offsetSeconds)
    return list.firstOrNull { it.startsWith("Etc/") } ?: list.minByOrNull { it.length }
  }

  /**
   * 本地時間 [local]（proleptic 格里曆）在 [tzid] 應採用的時差（秒）：`GMT ＝ local − 這個時差`。
   *
   * 夏令時間切換時，本地時間可能不存在或出現兩次，規則與 `java.time.ZonedDateTime.of` 相同：
   *  - **空隙**（撥快，例 02:30 不存在）：取切換**前**的時差 —— 等同把本地時間往後推一個空隙長度
   *  - **重疊**（撥慢，例 01:30 出現兩次）：取**較早**（切換前）的時差
   *
   * kotlinx-datetime 的 `LocalDateTime.toInstant(TimeZone)` 文件寫的是同一套規則（測試裡逐例對照），
   * 所以這個函式日後可以直接以 kotlinx 實作、不必 `expect`／`actual`。
   */
  fun offsetSecondsForLocal(tzid: String, local: LocalDateTime): Int {
    val ldt = local.toJavaLocalDateTime()
    val instant = ZonedDateTime.of(ldt, ZoneId.of(tzid)).toInstant()
    return (ldt.toEpochSecond(ZoneOffset.UTC) - instant.epochSecond).toInt()
  }
}
