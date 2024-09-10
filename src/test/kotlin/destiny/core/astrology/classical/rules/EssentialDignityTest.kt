/**
 * Created by smallufo on 2019-10-19.
 */
package destiny.core.astrology.classical.rules

import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class EssentialDignityTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun testName() {
    EssentialDignity.Ruler(Planet.SUN , ZodiacSign.LEO).also {
      assertEquals("Ruler" , it.getName(Locale.TAIWAN))
    }
  }
}
