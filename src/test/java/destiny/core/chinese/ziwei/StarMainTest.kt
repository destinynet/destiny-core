/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.getAbbreviation
import destiny.astrology.toString
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StarMainTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun listValues() {
    logger.info("{}" , StarMain.values)
  }

  @Test
  fun testToString() {
    assertEquals("天機", StarMain.天機.toString(Locale.TAIWAN))
    assertEquals("天机", StarMain.天機.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("天機", StarMain.天機.toString(Locale.ENGLISH))

    assertEquals("太陰", StarMain.太陰.toString(Locale.TAIWAN))
    assertEquals("太阴", StarMain.太陰.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("太陰", StarMain.太陰.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("機", StarMain.天機.getAbbreviation(Locale.TAIWAN))
    assertEquals("机", StarMain.天機.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("機", StarMain.天機.getAbbreviation(Locale.ENGLISH))

    assertEquals("陰", StarMain.太陰.getAbbreviation(Locale.TAIWAN))
    assertEquals("阴", StarMain.太陰.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("陰", StarMain.太陰.getAbbreviation(Locale.ENGLISH))
  }


  @Test
  fun testToStrings() {
    for (star in StarMain.values) {
      assertNotNull(star)
      assertNotNull(star.toString())
      assertNotNull(star.toString(Locale.TAIWAN))
      assertNotNull(star.toString(Locale.CHINA))
      logger.info("[{}] : tw = {}({}) , cn = {}({})",
        star.toString(),
        star.toString(Locale.TAIWAN), star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA), star.getAbbreviation(Locale.CHINA))
    }
  }
}
