/**
 * Created by kevin.huang on 2019-10-08.
 */
package destiny.astrology

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test

class astroPatternTranslatorTest {

  val logger = KotlinLogging.logger {}

  @Test
  fun getDescriptor() {
    val pattern = AstroPattern.GrandTrine(setOf(Planet.SUN , Planet.VENUS , Planet.MOON) , Element.WATER , null)
    astroPatternTranslator.getDescriptor(pattern).also { d ->
      logger.info("title = {}" , d.getTitle(Locale.TAIWAN))
      logger.info("desc = {}" , d.getDescription(Locale.TAIWAN))
    }
  }

}