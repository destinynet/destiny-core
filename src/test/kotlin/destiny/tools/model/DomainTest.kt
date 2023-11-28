/**
 * Created by smallufo on 2023-10-27.
 */
package destiny.tools.model

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainTest {

  @Test
  fun testGetTitle() {

    assertEquals("八字", Domain.EW.getTitle(Locale.TAIWAN))
    assertEquals("紫微斗數", Domain.ZIWEI.getTitle(Locale.TAIWAN))
    assertEquals("易經卜卦", Domain.ICHING_RAND.getTitle(Locale.TAIWAN))
    assertEquals("占星盤", Domain.HOROSCOPE.getTitle(Locale.TAIWAN))
    assertEquals("塔羅占卜", Domain.TAROT.getTitle(Locale.TAIWAN))
    assertEquals("求籤", Domain.CHANCE.getTitle(Locale.TAIWAN))

    assertEquals("Four Pillars", Domain.EW.getTitle(Locale.ENGLISH))
    assertEquals("Ziwei", Domain.ZIWEI.getTitle(Locale.ENGLISH))
    assertEquals("I-Ching", Domain.ICHING_RAND.getTitle(Locale.ENGLISH))
    assertEquals("Horoscope", Domain.HOROSCOPE.getTitle(Locale.ENGLISH))
    assertEquals("Tarot", Domain.TAROT.getTitle(Locale.ENGLISH))
    assertEquals("Oracle", Domain.CHANCE.getTitle(Locale.ENGLISH))


    assertEquals("塔羅占卜", Domain.TAROT.getTitle(Locale.GERMAN))
  }
}
