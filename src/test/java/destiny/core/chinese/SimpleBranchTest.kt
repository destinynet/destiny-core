/**
 * @author smallufo
 * Created on 2005/7/5 at 下午 01:02:34
 */
package destiny.core.chinese

import destiny.core.chinese.SimpleBranch.*
import org.junit.Assert.assertSame
import kotlin.test.Test

class SimpleBranchTest {
  @Test
  fun testGet() {
    assertSame(SimpleBranch.子, SimpleBranch.get(Branch.子))
  }

  @Test
  fun testGetFiveElement() {
    assertSame(FiveElement.水, 子.fiveElement)
    assertSame(FiveElement.土, 丑.fiveElement)
    assertSame(FiveElement.木, 寅.fiveElement)
    assertSame(FiveElement.木, 卯.fiveElement)
    assertSame(FiveElement.土, 辰.fiveElement)
    assertSame(FiveElement.火, 巳.fiveElement)
    assertSame(FiveElement.火, 午.fiveElement)
    assertSame(FiveElement.土, 未.fiveElement)
    assertSame(FiveElement.金, 申.fiveElement)
    assertSame(FiveElement.金, 酉.fiveElement)
    assertSame(FiveElement.土, 戌.fiveElement)
    assertSame(FiveElement.水, 亥.fiveElement)
  }

  @Test
  fun testGetFiveElementEarthlyBranches() {
    assertSame(FiveElement.水, getFiveElement(Branch.子))
    assertSame(FiveElement.土, getFiveElement(Branch.丑))
    assertSame(FiveElement.木, getFiveElement(Branch.寅))
    assertSame(FiveElement.木, getFiveElement(Branch.卯))
    assertSame(FiveElement.土, getFiveElement(Branch.辰))
    assertSame(FiveElement.火, getFiveElement(Branch.巳))
    assertSame(FiveElement.火, getFiveElement(Branch.午))
    assertSame(FiveElement.土, getFiveElement(Branch.未))
    assertSame(FiveElement.金, getFiveElement(Branch.申))
    assertSame(FiveElement.金, getFiveElement(Branch.酉))
    assertSame(FiveElement.土, getFiveElement(Branch.戌))
    assertSame(FiveElement.水, getFiveElement(Branch.亥))
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
