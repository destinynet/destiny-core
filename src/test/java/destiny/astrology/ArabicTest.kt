/**
 * Created by smallufo on 2020-03-05.
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ArabicTest {

  @Test
  fun testLocaleString() {

    assertEquals("福點", Arabic.Fortune.toString(Locale.TAIWAN))
    assertEquals("精神點", Arabic.Spirit.toString(Locale.TAIWAN))
    assertEquals("愛情點", Arabic.Eros.toString(Locale.TAIWAN))
    assertEquals("勝利點", Arabic.Victory.toString(Locale.TAIWAN))
    assertEquals("必要點", Arabic.Necessity.toString(Locale.TAIWAN))
    assertEquals("勇氣點", Arabic.Courage.toString(Locale.TAIWAN))
    assertEquals("復仇點", Arabic.Nemesis.toString(Locale.TAIWAN))

    assertEquals("福点", Arabic.Fortune.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("精神点", Arabic.Spirit.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("爱情点", Arabic.Eros.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("胜利点", Arabic.Victory.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("必要点", Arabic.Necessity.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("勇气点", Arabic.Courage.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("复仇点", Arabic.Nemesis.toString(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun testAbbreviation() {
    assertEquals("福", Arabic.Fortune.getAbbreviation(Locale.TAIWAN))
    assertEquals("精", Arabic.Spirit.getAbbreviation(Locale.TAIWAN))
    assertEquals("愛", Arabic.Eros.getAbbreviation(Locale.TAIWAN))
    assertEquals("勝", Arabic.Victory.getAbbreviation(Locale.TAIWAN))
    assertEquals("必", Arabic.Necessity.getAbbreviation(Locale.TAIWAN))
    assertEquals("勇", Arabic.Courage.getAbbreviation(Locale.TAIWAN))
    assertEquals("仇", Arabic.Nemesis.getAbbreviation(Locale.TAIWAN))

    assertEquals("福", Arabic.Fortune.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("精", Arabic.Spirit.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("爱", Arabic.Eros.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("胜", Arabic.Victory.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("必", Arabic.Necessity.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("勇", Arabic.Courage.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("仇", Arabic.Nemesis.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
  }
}
