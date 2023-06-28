/**
 * Created by smallufo on 2021-03-23.
 */
package destiny.core.chinese.lunarStation

import destiny.core.chinese.lunarStation.OppoHouse.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class OppoHouseTest {

  @Test
  fun testLoop() {
    assertSame(山, 山.next(0))
    assertSame(水, 山.next)
    assertSame(水, 山.next(1))
    assertSame(月, 山.next(11))
    assertSame(山, 山.next(12))
    assertSame(月, 山.next(-1))
    assertSame(山, 山.next(-12))

    assertSame(月, 月.next(0))
    assertSame(山, 月.next)
    assertSame(山, 月.next(1))
    assertSame(湯火, 月.next(11))
    assertSame(月, 月.next(12))
    assertSame(湯火, 月.next(-1))
    assertSame(月, 月.next(-12))
  }

  @Test
  fun testAheadOf() {
    assertEquals(0 , 山.getAheadOf(山))
    assertEquals(1 , 水.getAheadOf(山))
    assertEquals(11 , 月.getAheadOf(山))

    assertEquals(0 , 月.getAheadOf(月))
    assertEquals(1 , 山.getAheadOf(月))
    assertEquals(11 , 湯火.getAheadOf(月))
  }
}
