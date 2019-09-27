/**
 * Created by smallufo on 2019-09-27.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class EssentialDignityTest {

  val logger = KotlinLogging.logger {}

  @Test
  fun testRuler() {
    val ruler = EssentialDignity.Ruler(Planet.JUPITER , ZodiacSign.SAGITTARIUS)
    assertEquals("Ruler" , ruler.name)
  }

}