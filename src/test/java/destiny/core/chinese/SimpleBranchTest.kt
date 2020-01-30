/**
 * @author smallufo
 * Created on 2005/7/5 at 下午 01:02:34
 */
package destiny.core.chinese

import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.SimpleBranch.*
import destiny.core.chinese.SimpleBranch.Companion.getFiveElement
import org.junit.Assert.assertSame
import kotlin.test.Test

class SimpleBranchTest {
  @Test
  fun testGet() {
    assertSame(子, SimpleBranch[Branch.子])
  }

  @Test
  fun testGetFiveElement() {
    assertSame(水, 子.fiveElement)
    assertSame(土, 丑.fiveElement)
    assertSame(木, 寅.fiveElement)
    assertSame(木, 卯.fiveElement)
    assertSame(土, 辰.fiveElement)
    assertSame(火, 巳.fiveElement)
    assertSame(火, 午.fiveElement)
    assertSame(土, 未.fiveElement)
    assertSame(金, 申.fiveElement)
    assertSame(金, 酉.fiveElement)
    assertSame(土, 戌.fiveElement)
    assertSame(水, 亥.fiveElement)
  }

  @Test
  fun testGetFiveElementEarthlyBranches() {
    assertSame(水, getFiveElement(Branch.子))
    assertSame(土, getFiveElement(Branch.丑))
    assertSame(木, getFiveElement(Branch.寅))
    assertSame(木, getFiveElement(Branch.卯))
    assertSame(土, getFiveElement(Branch.辰))
    assertSame(火, getFiveElement(Branch.巳))
    assertSame(火, getFiveElement(Branch.午))
    assertSame(土, getFiveElement(Branch.未))
    assertSame(金, getFiveElement(Branch.申))
    assertSame(金, getFiveElement(Branch.酉))
    assertSame(土, getFiveElement(Branch.戌))
    assertSame(水, getFiveElement(Branch.亥))
  }

  @Test
  fun testGetYinYang() {
    assertSame(true, 子.booleanValue)
    assertSame(false, 丑.booleanValue)
    assertSame(true, 寅.booleanValue)
    assertSame(false, 卯.booleanValue)
    assertSame(true, 辰.booleanValue)
    assertSame(false, 巳.booleanValue)
    assertSame(true, 午.booleanValue)
    assertSame(false, 未.booleanValue)
    assertSame(true, 申.booleanValue)
    assertSame(false, 酉.booleanValue)
    assertSame(true, 戌.booleanValue)
    assertSame(false, 亥.booleanValue)
  }

}
