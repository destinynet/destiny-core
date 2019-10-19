/**
 * Created by smallufo on 2019-10-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class EssentialDignityTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun testName() {
    EssentialDignity.Ruler(Planet.SUN , ZodiacSign.LEO).also {
      assertEquals("Ruler" , it.name)
    }
  }
}
