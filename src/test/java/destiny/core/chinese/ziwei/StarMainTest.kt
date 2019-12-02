/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.toString
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class StarMainTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun listValues() {
    logger.info("{}" , StarMain.values)
  }

  @Test
  fun testToString() {
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
