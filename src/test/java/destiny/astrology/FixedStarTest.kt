/**
 * @author smallufo
 * Created on 2008/1/16 at 上午 12:21:43
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class FixedStarTest {

  @Test
  fun testFixedStar() {

    FixedStar.array.forEach {
      assertNotNull(it)
    }

    for (each in FixedStar.array) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])

      //System.out.println(each.toString() + ":" + each.getAbbreviation() + ":" + each.getAbbreviation(locale));
      assertNotNull(each.abbreviation)
      assertNotSame('!', each.abbreviation[0])

      assertNotNull(each.getAbbreviation(locale))
      assertNotSame('!', each.getAbbreviation(locale)[0])
    }
  }
}
