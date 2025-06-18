/**
 * Created by smallufo on 2024-02-13.
 */
package destiny.tools.ai.model

import destiny.tools.ai.model.Domain.*
import destiny.tools.ai.model.Domain.Bdnp.*
import destiny.tools.ai.model.Domain.Period.DAILY_HOROSCOPE
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainTest {

  @Test
  fun testGetTitle() {

    assertEquals("八字", EW.getTitle(Locale.TAIWAN))
    assertEquals("紫微斗數", ZIWEI.getTitle(Locale.TAIWAN))
    assertEquals("占星", HOROSCOPE.getTitle(Locale.TAIWAN))
    assertEquals("易經卜卦", ICHING_RAND.getTitle(Locale.TAIWAN))
    assertEquals("塔羅占卜", TAROT.getTitle(Locale.TAIWAN))
    assertEquals("求籤", CHANCE.getTitle(Locale.TAIWAN))
    assertEquals("風水", FENGSHUI_AERIAL.getTitle(Locale.TAIWAN))
    assertEquals("占星骰子", AstroDice.ASTRO_DICE_SIMPLE.getTitle(Locale.TAIWAN))
    assertEquals("占星骰子", AstroDice.ASTRO_DICE_ADVANCED.getTitle(Locale.TAIWAN))
    assertEquals("每日運勢", DAILY_HOROSCOPE.getTitle(Locale.TAIWAN))
    assertEquals("擇日" , Electional.ELECTIONAL_DAY_HOUR.getTitle(Locale.TAIWAN))


    assertEquals("Four Pillars", EW.getTitle(Locale.ENGLISH))
    assertEquals("Ziwei", ZIWEI.getTitle(Locale.ENGLISH))
    assertEquals("Horoscope", HOROSCOPE.getTitle(Locale.ENGLISH))
    assertEquals("I-Ching", ICHING_RAND.getTitle(Locale.ENGLISH))
    assertEquals("Tarot", TAROT.getTitle(Locale.ENGLISH))
    assertEquals("Oracle", CHANCE.getTitle(Locale.ENGLISH))
    assertEquals("Feng Shui", FENGSHUI_AERIAL.getTitle(Locale.ENGLISH))
    assertEquals("塔羅占卜", TAROT.getTitle(Locale.GERMAN))
    assertEquals("Daily Horoscope", DAILY_HOROSCOPE.getTitle(Locale.ENGLISH))
    assertEquals("Electional", Electional.ELECTIONAL_DAY_HOUR.getTitle(Locale.ENGLISH))
  }

}

