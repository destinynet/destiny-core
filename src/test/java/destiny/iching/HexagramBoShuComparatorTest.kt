/**
 * Created by smallufo on 2018-01-11.
 */
package destiny.iching

import kotlin.test.Test
import kotlin.test.assertSame

class HexagramBoShuComparatorTest {

  @Test
  fun testSeq() {
    val comparator = HexagramBoShuComparator()

    assertSame(Hexagram.乾, comparator.getHexagram(1))
    assertSame(Hexagram.否, comparator.getHexagram(2))
    assertSame(Hexagram.家人, comparator.getHexagram(63))
    assertSame(Hexagram.益, comparator.getHexagram(64))

    assertSame(Hexagram.家人, comparator.getHexagram(-1))
    assertSame(Hexagram.益, comparator.getHexagram(0))
    assertSame(Hexagram.乾, comparator.getHexagram(65))
    assertSame(Hexagram.否, comparator.getHexagram(66))
  }
}