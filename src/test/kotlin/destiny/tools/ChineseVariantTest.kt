/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.tools

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChineseVariantTest {

  @Test
  fun ofLang() {
    listOf("zh-CN", "zh-SG", "zh-Hans", "zh-Hans-HK", "zh-Hant-CN").forEach {
      assertEquals(ChineseVariant.SIMPLIFIED, ChineseVariant.of(Lang.of(it)!!), it)
    }
    listOf("zh-HK", "zh-MO", "zh-Hant-HK").forEach {
      assertEquals(ChineseVariant.HONG_KONG, ChineseVariant.of(Lang.of(it)!!), it)
    }
    listOf("zh", "zh-TW", "zh-Hant", "zh_TW").forEach {
      assertEquals(ChineseVariant.TAIWAN, ChineseVariant.of(Lang.of(it)!!), it)
    }
    listOf("en", "ja", "en-HK").forEach { assertNull(ChineseVariant.of(Lang.of(it)!!), it) }
    assertNull(ChineseVariant.of(Lang.ROOT))
  }

  @Test
  fun ofLocale() {
    assertEquals(ChineseVariant.SIMPLIFIED, ChineseVariant.of(Locale.SIMPLIFIED_CHINESE))
    assertEquals(ChineseVariant.TAIWAN, ChineseVariant.of(Locale.TRADITIONAL_CHINESE))
    assertEquals(ChineseVariant.TAIWAN, ChineseVariant.of(Locale.CHINESE))
    assertEquals(ChineseVariant.HONG_KONG, ChineseVariant.of(Locale.forLanguageTag("zh-MO")))
    assertNull(ChineseVariant.of(Locale.ENGLISH))
    assertNull(ChineseVariant.of(Locale.ROOT))
  }
}
