/**
 * Created by smallufo on 2020-03-05.
 */
package destiny.core.astrology

import destiny.core.AbstractPointTest
import destiny.core.getAbbreviation
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LotTest : AbstractPointTest(Lot::class) {

  @Test
  fun testSerialize() {
    Lot.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }


  @Test
  fun testToStringLocale() {

    assertEquals("福點", Lot.Fortune.getTitle(Locale.TAIWAN))
    assertEquals("精神點", Lot.Spirit.getTitle(Locale.TAIWAN))
    assertEquals("愛情點", Lot.Eros.getTitle(Locale.TAIWAN))
    assertEquals("勝利點", Lot.Victory.getTitle(Locale.TAIWAN))
    assertEquals("必要點", Lot.Necessity.getTitle(Locale.TAIWAN))
    assertEquals("勇氣點", Lot.Courage.getTitle(Locale.TAIWAN))
    assertEquals("復仇點", Lot.Nemesis.getTitle(Locale.TAIWAN))

    assertEquals("福点", Lot.Fortune.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("精神点", Lot.Spirit.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("爱情点", Lot.Eros.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("胜利点", Lot.Victory.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("必要点", Lot.Necessity.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("勇气点", Lot.Courage.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("复仇点", Lot.Nemesis.getTitle(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun testAbbreviation() {
    assertEquals("福", Lot.Fortune.getAbbreviation(Locale.TAIWAN))
    assertEquals("精", Lot.Spirit.getAbbreviation(Locale.TAIWAN))
    assertEquals("愛", Lot.Eros.getAbbreviation(Locale.TAIWAN))
    assertEquals("勝", Lot.Victory.getAbbreviation(Locale.TAIWAN))
    assertEquals("必", Lot.Necessity.getAbbreviation(Locale.TAIWAN))
    assertEquals("勇", Lot.Courage.getAbbreviation(Locale.TAIWAN))
    assertEquals("仇", Lot.Nemesis.getAbbreviation(Locale.TAIWAN))

    assertEquals("福", Lot.Fortune.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("精", Lot.Spirit.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("爱", Lot.Eros.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("胜", Lot.Victory.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("必", Lot.Necessity.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("勇", Lot.Courage.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("仇", Lot.Nemesis.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
  }
}
