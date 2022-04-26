/**
 * @author smallufo
 * Created on 2008/1/16 at 上午 12:21:43
 */
package destiny.core.astrology

import destiny.core.AbstractPointTest
import destiny.core.getAbbreviation
import destiny.core.toString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FixedStarTest : AbstractPointTest(FixedStar::class) {

  @Test
  fun testToStringLocale() {

    assertEquals("畢宿五", FixedStar.ALDEBARAN.toString(Locale.TAIWAN))
    assertEquals("毕宿五", FixedStar.ALDEBARAN.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Aldebaran", FixedStar.ALDEBARAN.toString(Locale.ENGLISH))
    //assertEquals("Aldebaran", FixedStar.ALDEBARAN.toString(Locale.FRANCE))

    assertEquals("畢", FixedStar.ALDEBARAN.getAbbreviation(Locale.TAIWAN))
    assertEquals("毕", FixedStar.ALDEBARAN.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Ald", FixedStar.ALDEBARAN.getAbbreviation(Locale.ENGLISH))
    //assertEquals("Ald", FixedStar.ALDEBARAN.getAbbreviation(Locale.FRANCE))

  }

}
