/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.astrology.getAbbreviation
import destiny.core.astrology.toString
import destiny.core.chinese.ziwei.StarMain.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StarMainTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testCompare() {
    assertEquals(listOf(紫微, 天機), sortedSetOf(紫微, 天機).toList())
    assertEquals(listOf(紫微, 天機), sortedSetOf(天機, 紫微).toList())

    val shuffled = StarMain.values.toList().shuffled()
    logger.info { "shuffled = $shuffled" }
    shuffled.sorted().zip(StarMain.values.toList()).forEach { pair: Pair<StarMain, StarMain> ->
      assertEquals(pair.first , pair.second)
    }
  }

  @Test
  fun listValues() {
    logger.info("{}" , StarMain.values)
  }

  @Test
  fun testToString() {
    assertEquals("天機", 天機.toString(Locale.TAIWAN))
    assertEquals("天机", 天機.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("天機", 天機.toString(Locale.ENGLISH))

    assertEquals("太陰", 太陰.toString(Locale.TAIWAN))
    assertEquals("太阴", 太陰.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("太陰", 太陰.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("機", 天機.getAbbreviation(Locale.TAIWAN))
    assertEquals("机", 天機.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("機", 天機.getAbbreviation(Locale.ENGLISH))

    assertEquals("陰", 太陰.getAbbreviation(Locale.TAIWAN))
    assertEquals("阴", 太陰.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("陰", 太陰.getAbbreviation(Locale.ENGLISH))
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
