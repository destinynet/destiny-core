/**
 * Created by smallufo on 2021-03-23.
 */
package destiny.core.chinese.lunarStation

import destiny.core.chinese.lunarStation.SelfHouse.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class SelfHouseTest {

  @Test
  fun testLoop() {
    assertSame(山, 山.next(0))
    assertSame(林, 山.next)
    assertSame(林, 山.next(1))
    assertSame(田, 山.next(11))
    assertSame(山, 山.next(12))
    assertSame(田, 山.next(-1))
    assertSame(山, 山.next(-12))

    assertSame(田, 田.next(0))
    assertSame(山, 田.next)
    assertSame(山, 田.next(1))
    assertSame(湖, 田.next(11))
    assertSame(田, 田.next(12))
    assertSame(湖, 田.next(-1))
    assertSame(田, 田.next(-12))
  }

  @Test
  fun testAheadOf() {
    assertEquals(0 , 山.getAheadOf(山))
    assertEquals(1 , 林.getAheadOf(山))
    assertEquals(11 , 田.getAheadOf(山))

    assertEquals(0 , 田.getAheadOf(田))
    assertEquals(1 , 山.getAheadOf(田))
    assertEquals(11 , 湖.getAheadOf(田))
  }
}
