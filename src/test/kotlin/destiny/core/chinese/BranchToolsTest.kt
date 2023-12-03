/**
 * Created by smallufo on 2023-11-30.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.BranchTools.direction
import destiny.core.chinese.BranchTools.trilogy
import destiny.core.chinese.FiveElement.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BranchToolsTest {

  @Test
  fun trilogyTest() {
    assertEquals(水, trilogy(申))
    assertEquals(水, trilogy(子))
    assertEquals(水, trilogy(辰))

    assertEquals(木, trilogy(亥))
    assertEquals(木, trilogy(卯))
    assertEquals(木, trilogy(未))

    assertEquals(金, trilogy(巳))
    assertEquals(金, trilogy(酉))
    assertEquals(金, trilogy(丑))

    assertEquals(火, trilogy(寅))
    assertEquals(火, trilogy(午))
    assertEquals(火, trilogy(戌))
  }

  @Test
  fun trilogyBranchesTest() {
    assertEquals(木, trilogy(亥, 卯, 未))
    assertEquals(水, trilogy(申, 子, 辰))
    assertEquals(金, trilogy(巳, 酉, 丑))
    assertEquals(火, trilogy(寅, 午, 戌))
    assertNull(trilogy(寅, 午, 子))
    assertNull(trilogy(寅, 午, 午))
  }

  @Test
  fun directionTest() {
    assertEquals(水, direction(亥))
    assertEquals(水, direction(子))
    assertEquals(水, direction(丑))

    assertEquals(木, direction(寅))
    assertEquals(木, direction(卯))
    assertEquals(木, direction(辰))

    assertEquals(火, direction(巳))
    assertEquals(火, direction(午))
    assertEquals(火, direction(未))

    assertEquals(金, direction(申))
    assertEquals(金, direction(酉))
    assertEquals(金, direction(戌))
  }

  @Test
  fun directionsTest() {
    assertEquals(水, direction(亥, 子, 丑))
    assertEquals(木, direction(寅, 卯, 辰))
    assertEquals(火, direction(巳, 午, 未))
    assertEquals(金, direction(申, 酉, 戌))
    assertNull(direction(亥, 子, 寅))
    assertNull(direction(亥, 子, 子))
  }
}
