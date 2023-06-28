/**
 * Created by smallufo on 2018-01-11.
 */
package destiny.core.iching

import kotlin.test.Test
import kotlin.test.assertSame

class HexagramGuiCangComparatorTest {

  @Test
  fun testSeq() {
    val comparator = HexagramGuiCangComparator()

    assertSame(Hexagram.坤, comparator.getHexagram(1))
    assertSame(Hexagram.乾, comparator.getHexagram(2))
    assertSame(Hexagram.蠱, comparator.getHexagram(63))
    assertSame(Hexagram.隨, comparator.getHexagram(64))

    assertSame(Hexagram.蠱, comparator.getHexagram(-1))
    assertSame(Hexagram.隨, comparator.getHexagram(0))
    assertSame(Hexagram.坤, comparator.getHexagram(65))
    assertSame(Hexagram.乾, comparator.getHexagram(66))
  }
}
