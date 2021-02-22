/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class CStarTest {

  @Test
  fun testName() {
    CStar::class.nestedClasses.map { k ->
      k.objectInstance as CStar
    }.forEach { cStar ->
      assertTrue(cStar.toString(Locale.TAIWAN).length == 1)
      assertTrue(cStar.toString(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(cStar.toString(Locale.ENGLISH))
    }
  }
}
