/**
 * Created by smallufo on 2024-09-12.
 */
package destiny.core

import destiny.core.astrology.Planet.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CircularTest {

  @Test
  fun testCircularList() {
    val list1 = Circular.of(SATURN, VENUS, JUPITER, MARS)
    val list2 = list1.rotateLeft(1)
    val list3 = list1.rotateRight(1)
    val list4 = Circular.of(VENUS, JUPITER, MARS, SATURN)
    assertEquals(list1, list2)
    assertEquals(list2, list3)
    assertEquals(list3, list4)
    assertEquals(list4, list1)
    val list5 = Circular.of(VENUS, JUPITER, MARS, SATURN, SUN)
    assertNotEquals(list5, list4)
  }
}
