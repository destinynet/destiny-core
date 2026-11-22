/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import destiny.tools.Lang
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

/** 預設地點表改以 [Lang] 為 key 之後，`Locale` 與 `Lang` 兩個入口挑到的地點相同 */
class LocationOfLangTest {

  @Test
  fun `語系挑預設地點`() {
    mapOf(
      Locale.TAIWAN to "Asia/Taipei", Locale.CHINA to "Asia/Shanghai", Locale.of("zh", "HK") to "Asia/Hong_Kong",
      Locale.of("zh", "SG") to "Asia/Singapore", Locale.JAPAN to "Asia/Tokyo", Locale.US to "America/New_York",
      Locale.of("en", "AU") to "Australia/Sydney", Locale.UK to "Europe/London", Locale.FRANCE to "Europe/Paris",
      Locale.of("en", "XX") to "America/New_York",     // 地區比對不到 → 只比語言
      Locale.of("es", "ES") to "Asia/Taipei",          // 語言也比對不到 → 台北
    ).forEach { (locale, tzid) ->
      assertEquals(tzid, locationOf(locale).tzid, "$locale")
      assertEquals(locationOf(locale), locationOf(Lang.of(locale.toLanguageTag())!!), "$locale")
    }
  }

  @Test
  fun `effectiveTzid 與 zoneId`() {
    assertEquals("Asia/Taipei", Location.of(25.0, 121.5, "Asia/Taipei").effectiveTzid)
    assertEquals("Etc/GMT-8", Location.of(25.0, 121.5, minuteOffset = 480).effectiveTzid)
    assertEquals("GMT", Location.of(25.0, 121.5).effectiveTzid)
    assertEquals(GMT, Location.of(25.0, 121.5).zoneId)
  }
}
