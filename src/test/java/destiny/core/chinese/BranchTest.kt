/*
 * @author smallufo
 * Created on 2004/11/20 at 上午 07:13:48
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import java.util.*
import kotlin.test.*

class BranchTest {

  @Test
  fun testGetAheadOf() {
    assertSame(0, 子.getAheadOf(子))
    assertSame(1, 子.getAheadOf(亥))
    assertSame(2, 子.getAheadOf(戌))
    assertSame(3, 子.getAheadOf(酉))
    assertSame(4, 子.getAheadOf(申))
    assertSame(5, 子.getAheadOf(未))
    assertSame(6, 子.getAheadOf(午))
    assertSame(7, 子.getAheadOf(巳))
    assertSame(8, 子.getAheadOf(辰))
    assertSame(9, 子.getAheadOf(卯))
    assertSame(10, 子.getAheadOf(寅))
    assertSame(11, 子.getAheadOf(丑))

    assertSame(0, 亥.getAheadOf(亥))
    assertSame(1, 亥.getAheadOf(戌))
    assertSame(2, 亥.getAheadOf(酉))
    assertSame(3, 亥.getAheadOf(申))
    assertSame(4, 亥.getAheadOf(未))
    assertSame(5, 亥.getAheadOf(午))
    assertSame(6, 亥.getAheadOf(巳))
    assertSame(7, 亥.getAheadOf(辰))
    assertSame(8, 亥.getAheadOf(卯))
    assertSame(9, 亥.getAheadOf(寅))
    assertSame(10, 亥.getAheadOf(丑))
    assertSame(11, 亥.getAheadOf(子))
  }

  @Test
  fun testNext() {
    assertSame(子, 子.next(0))
    assertSame(丑, 子.next(1))
    assertSame(亥, 子.next(11))
    assertSame(子, 子.next(12))
    assertSame(子, 子.next(120))

    assertSame(亥, 亥.next(0))
    assertSame(子, 亥.next(1))
    assertSame(戌, 亥.next(11))
    assertSame(亥, 亥.next(12))
    assertSame(亥, 亥.next(120))
  }

  @Test
  fun testPrev() {
    assertSame(子, 子.prev(0))
    assertSame(亥, 子.prev(1))
    assertSame(丑, 子.prev(11))
    assertSame(子, 子.prev(12))
    assertSame(子, 子.prev(120))

    assertSame(亥, 亥.prev(0))
    assertSame(戌, 亥.prev(1))
    assertSame(子, 亥.prev(11))
    assertSame(亥, 亥.prev(12))
    assertSame(亥, 亥.prev(120))

  }


  @Test
  fun testGetEarthlyBranchesFromInt() {
    assertSame(子, Branch.get(-24))
    assertSame(子, Branch.get(-12))
    assertSame(亥, Branch.get(-1))
    assertSame(子, Branch.get(0))
    assertSame(丑, Branch.get(1))
    assertSame(寅, Branch.get(2))
    assertSame(卯, Branch.get(3))
    assertSame(辰, Branch.get(4))
    assertSame(巳, Branch.get(5))
    assertSame(午, Branch.get(6))
    assertSame(未, Branch.get(7))
    assertSame(申, Branch.get(8))
    assertSame(酉, Branch.get(9))
    assertSame(戌, Branch.get(10))
    assertSame(亥, Branch.get(11))
    assertSame(子, Branch.get(12))
    assertSame(子, Branch.get(24))
    assertSame(子, Branch.get(36))
  }

  @Test
  fun testGetEarthlyBranchesFromChar() {
    assertSame(子, Branch.get('子'))
    assertSame(丑, Branch.get('丑'))
    assertSame(寅, Branch.get('寅'))
    assertSame(卯, Branch.get('卯'))
    assertSame(辰, Branch.get('辰'))
    assertSame(巳, Branch.get('巳'))
    assertSame(午, Branch.get('午'))
    assertSame(未, Branch.get('未'))
    assertSame(申, Branch.get('申'))
    assertSame(酉, Branch.get('酉'))
    assertSame(戌, Branch.get('戌'))
    assertSame(亥, Branch.get('亥'))
    assertNull(Branch.get('無'))
  }

  @Test
  fun testGetIndexStatic() {
    assertSame(0, Branch.getIndex(子))
    assertSame(1, Branch.getIndex(丑))
    assertSame(2, Branch.getIndex(寅))
    assertSame(3, Branch.getIndex(卯))
    assertSame(4, Branch.getIndex(辰))
    assertSame(5, Branch.getIndex(巳))
    assertSame(6, Branch.getIndex(午))
    assertSame(7, Branch.getIndex(未))
    assertSame(8, Branch.getIndex(申))
    assertSame(9, Branch.getIndex(酉))
    assertSame(10, Branch.getIndex(戌))
    assertSame(11, Branch.getIndex(亥))
  }

  @Test
  fun testGetIndexDynamic() {
    assertSame(0, 子.index)
    assertSame(1, 丑.index)
    assertSame(2, 寅.index)
    assertSame(3, 卯.index)
    assertSame(4, 辰.index)
    assertSame(5, 巳.index)
    assertSame(6, 午.index)
    assertSame(7, 未.index)
    assertSame(8, 申.index)
    assertSame(9, 酉.index)
    assertSame(10, 戌.index)
    assertSame(11, 亥.index)

  }

  @Test
  fun testSorting() {
    val EBArray = arrayOf(午, 酉, 子, 卯)
    Arrays.sort(EBArray)
    val expected = arrayOf(子, 卯, 午, 酉)
    assertTrue(Arrays.equals(expected, EBArray))
  }

  @Test
  fun testEarthlyBranches() {
    assertEquals("子", 子.toString())
    assertEquals(子, Branch.get('子'))
    assertSame(子, Branch.get('子'))
    assertEquals(子, Branch.get(0))
    assertSame(子, Branch.get(0))
    assertSame(子, Branch.get(12))
    assertSame(子, Branch.get(-12))
    assertSame(子, Branch.get(-24))

    assertEquals("亥", 亥.toString())
    assertEquals(亥, Branch.get('亥'))
    assertSame(亥, Branch.get('亥'))
    assertEquals(亥, Branch.get(11))
    assertSame(亥, Branch.get(11))
    assertSame(亥, Branch.get(23))
    assertSame(亥, Branch.get(-1))
    assertSame(亥, Branch.get(-13))

  }
}

