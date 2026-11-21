/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.ZoneId
import kotlin.math.abs
import kotlin.test.*
import kotlin.time.Instant

class TimeZoneRulesTest {

  /**
   * 夏令時間切換的前後、空隙中、重疊中：[TimeZoneRules.offsetSecondsForLocal]（java.time 規則）
   * 必須與 kotlinx 的 `toInstant(TimeZone)` 完全一致 —— 這是「本地 → GMT 日後可以直接用 kotlinx」的依據。
   */
  @Test
  fun `空隙與重疊的處理與 kotlinx 一致`() {
    val zones = listOf(
      "Asia/Taipei" to (1945..1980), "Asia/Shanghai" to (1940..1992), "Asia/Hong_Kong" to (1941..1980),
      "Asia/Tokyo" to (1948..1952), "Europe/London" to (1916..1950), "Europe/Berlin" to (1940..1950),
      "America/New_York" to (1918..2030), "Australia/Sydney" to (1971..2030), "Pacific/Auckland" to (1974..2030),
      "America/Sao_Paulo" to (1985..2019), "Asia/Kolkata" to (1940..1946),
    )
    var checked = 0
    zones.forEach { (tz, years) ->
      val kz = TimeZone.of(tz)
      ZoneId.of(tz).rules.transitions.filter { it.dateTimeBefore.year in years }.forEach { t ->
        val span = abs(t.duration.seconds)
        listOf(t.dateTimeBefore.minusMinutes(1), t.dateTimeBefore, t.dateTimeBefore.plusSeconds(span / 2),
               t.dateTimeBefore.plusSeconds(span).minusMinutes(1), t.dateTimeAfter, t.dateTimeAfter.plusMinutes(1))
          .forEach { j ->
            val local = j.toKotlinLocalDateTime()
            val viaKotlinx = (local.toInstant(TimeZone.UTC).epochSeconds - local.toInstant(kz).epochSeconds).toInt()
            assertEquals(viaKotlinx, TimeZoneRules.offsetSecondsForLocal(tz, local), "$tz $local（${if (t.isGap) "空隙" else "重疊"}）")
            checked++
          }
      }
    }
    assertTrue(checked > 1000, "對照筆數太少：$checked")
  }

  @Test
  fun `空隙：紐約 2021-03-14 凌晨兩點半不存在，取切換前的時差（-5h）`() {
    assertEquals(-5 * 3600, TimeZoneRules.offsetSecondsForLocal("America/New_York", LocalDateTime(2021, 3, 14, 2, 30)))
  }

  @Test
  fun `重疊：紐約 2021-11-07 凌晨一點半出現兩次，取較早的時差（-4h）`() {
    assertEquals(-4 * 3600, TimeZoneRules.offsetSecondsForLocal("America/New_York", LocalDateTime(2021, 11, 7, 1, 30)))
  }

  @Test
  fun `標準時差與夏令時間`() {
    val julyNy = Instant.parse("2021-07-01T12:00:00Z")
    assertEquals(-4 * 3600, TimeZoneRules.offsetSeconds("America/New_York", julyNy))
    assertEquals(-5 * 3600, TimeZoneRules.standardOffsetSeconds("America/New_York", julyNy))
    assertEquals(3600, TimeZoneRules.daylightSavingSeconds("America/New_York", julyNy))
    assertTrue(TimeZoneRules.isDaylightSaving("America/New_York", julyNy))

    val julyTaipei1975 = Instant.parse("1975-07-01T00:00:00Z")   // 台灣 1975 年有夏令時間
    assertTrue(TimeZoneRules.isDaylightSaving("Asia/Taipei", julyTaipei1975))
    assertEquals(8 * 3600, TimeZoneRules.standardOffsetSeconds("Asia/Taipei", julyTaipei1975))
  }

  @Test
  fun `下一次切換`() {
    val next = TimeZoneRules.nextTransition("America/New_York", Instant.parse("2021-01-01T00:00:00Z"))
    assertEquals(Instant.parse("2021-03-14T07:00:00Z"), next)
    assertNull(TimeZoneRules.nextTransition("Asia/Taipei", Instant.parse("2021-01-01T00:00:00Z")))
  }
}
