package destiny.core.chinese.holo

import destiny.core.chinese.holo.HoloFeature.Companion.shrink
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 天數、地數化為起卦之數：「其數大於二十五或三十，則先減去二十五或三十（或其倍數）」，
 * 「天數剛好二十五，以五數起卦」「地數剛好三十，以三數起卦」，個位為 0 則取十位。
 */
class HoloShrinkTest {

  @Test
  fun heaven() {
    assertEquals(1, shrink(1, 25))
    assertEquals(9, shrink(9, 25))
    assertEquals(1, shrink(10, 25))
    assertEquals(4, shrink(14, 25))
    assertEquals(2, shrink(20, 25))
    assertEquals(5, shrink(25, 25))
    assertEquals(3, shrink(28, 25))
    assertEquals(9, shrink(44, 25))
    assertEquals(2, shrink(45, 25))
    // 50 減 25 得 25，不再減，取 5（寄宮）；舊算法 50 % 25 = 0 → 落到「離」
    assertEquals(5, shrink(50, 25))
    assertEquals(1, shrink(51, 25))
    assertEquals(5, shrink(75, 25))
  }

  @Test
  fun earth() {
    assertEquals(2, shrink(2, 30))
    assertEquals(1, shrink(10, 30))
    assertEquals(2, shrink(20, 30))
    assertEquals(3, shrink(30, 30))
    assertEquals(4, shrink(34, 30))
    assertEquals(8, shrink(38, 30))
    assertEquals(1, shrink(40, 30))
    assertEquals(2, shrink(42, 30))
    assertEquals(2, shrink(50, 30))
    // 60 減 30 得 30，取 3；舊算法 60 % 30 = 0 → 落到「離」
    assertEquals(3, shrink(60, 30))
    assertEquals(2, shrink(62, 30))
  }
}
