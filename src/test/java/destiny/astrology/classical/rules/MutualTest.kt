/**
 * Created by kevin.huang on 2019-09-27.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class MutualTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun testName() {
    val mutualReception = Mutual.Reception.Equal(Planet.SUN , ZodiacSign.ARIES , null , Planet.MARS , ZodiacSign.LEO , null , Dignity.RULER)
    assertEquals("Equal" , mutualReception.name)
    logger.info("name = {}" , mutualReception.javaClass.name)
  }
}