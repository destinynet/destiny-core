/**
 * Created by smallufo on 2023-11-30.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
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
}
