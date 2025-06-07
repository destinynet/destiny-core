/*
 * @author smallufo
 * Created on 2004/11/20 at 上午 07:13:48
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import java.util.*
import kotlin.test.*

class BranchTest {

  @Test
  fun testOpposite() {
    assertSame(午, 子.opposite)
    assertSame(未, 丑.opposite)
    assertSame(申, 寅.opposite)
    assertSame(酉, 卯.opposite)
    assertSame(戌, 辰.opposite)
    assertSame(亥, 巳.opposite)
    assertSame(子, 午.opposite)
    assertSame(丑, 未.opposite)
    assertSame(寅, 申.opposite)
    assertSame(卯, 酉.opposite)
    assertSame(辰, 戌.opposite)
    assertSame(巳, 亥.opposite)
  }

  @Test
  fun testCombined() {
    assertSame(丑, 子.combined)
    assertSame(子, 丑.combined)
    assertSame(亥, 寅.combined)
    assertSame(戌, 卯.combined)
    assertSame(酉, 辰.combined)
    assertSame(申, 巳.combined)
    assertSame(未, 午.combined)
    assertSame(午, 未.combined)
    assertSame(巳, 申.combined)
    assertSame(辰, 酉.combined)
    assertSame(卯, 戌.combined)
    assertSame(寅, 亥.combined)
  }


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

    assertSame(丑, 子.next)
    assertSame(子, 亥.next)

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
    assertSame(亥, 子.prev)

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
    assertSame(子, Branch[-24])
    assertSame(子, Branch[-12])
    assertSame(亥, Branch[-1])
    assertSame(子, Branch[0])
    assertSame(丑, Branch[1])
    assertSame(寅, Branch[2])
    assertSame(卯, Branch[3])
    assertSame(辰, Branch[4])
    assertSame(巳, Branch[5])
    assertSame(午, Branch[6])
    assertSame(未, Branch[7])
    assertSame(申, Branch[8])
    assertSame(酉, Branch[9])
    assertSame(戌, Branch[10])
    assertSame(亥, Branch[11])
    assertSame(子, Branch[12])
    assertSame(子, Branch[24])
    assertSame(子, Branch[36])
  }

  @Test
  fun testGetEarthlyBranchesFromChar() {
    assertSame(子, Branch['子'])
    assertSame(丑, Branch['丑'])
    assertSame(寅, Branch['寅'])
    assertSame(卯, Branch['卯'])
    assertSame(辰, Branch['辰'])
    assertSame(巳, Branch['巳'])
    assertSame(午, Branch['午'])
    assertSame(未, Branch['未'])
    assertSame(申, Branch['申'])
    assertSame(酉, Branch['酉'])
    assertSame(戌, Branch['戌'])
    assertSame(亥, Branch['亥'])
    assertNull(Branch['無'])
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
  fun testQuads() {
    assertEquals(setOf(子, 午, 卯, 酉), 子.quads)
    assertEquals(setOf(丑, 未, 辰, 戌), 丑.quads)
    assertEquals(setOf(寅, 巳, 申, 亥), 寅.quads)
  }

  @Test
  fun testTrine() {
    assertSame(戌, setOf(寅, 午).trine())
    assertSame(辰, setOf(申, 子).trine())
    assertSame(未, setOf(亥, 卯).trine())
    assertSame(丑, setOf(巳, 酉).trine())
    assertNull(setOf(子, 丑).trine())
    assertNull(setOf(寅, 卯).trine())
    assertNull(setOf(辰, 巳).trine())
    assertNull(setOf(午, 未).trine())
    assertNull(setOf(申, 酉).trine())
    assertNull(setOf(戌, 亥).trine())
  }

  @Test
  fun testTrinities() {
    assertEquals(setOf(申, 子, 辰), 子.trinities)
    assertEquals(setOf(巳, 酉, 丑), 丑.trinities)
    assertEquals(setOf(寅, 午, 戌), 寅.trinities)
    assertEquals(setOf(亥, 卯, 未), 卯.trinities)
  }

  @Test
  fun testGrip() {
    assertSame(丑, setOf(子, 寅).grip())
    assertSame(寅, setOf(丑, 卯).grip())
    assertSame(卯, setOf(寅, 辰).grip())
    assertSame(辰, setOf(卯, 巳).grip())
    assertSame(巳, setOf(辰, 午).grip())
    assertSame(午, setOf(巳, 未).grip())
    assertSame(未, setOf(午, 申).grip())
    assertSame(申, setOf(未, 酉).grip())
    assertSame(酉, setOf(申, 戌).grip())
    assertSame(戌, setOf(酉, 亥).grip())
    assertSame(亥, setOf(戌, 子).grip())
    assertSame(子, setOf(亥, 丑).grip())

    assertNull(setOf(子, 卯).grip())
  }

  @Test
  fun testSorting() {
    val array = arrayOf(午, 酉, 子, 卯)
    Arrays.sort(array)
    val expected = arrayOf(子, 卯, 午, 酉)
    assertTrue(expected.contentEquals(array))
  }

  @Test
  fun testEarthlyBranches() {
    assertEquals("子", 子.toString())
    assertEquals(子, Branch['子'])
    assertSame(子, Branch['子'])
    assertEquals(子, Branch[0])
    assertSame(子, Branch[0])
    assertSame(子, Branch[12])
    assertSame(子, Branch[-12])
    assertSame(子, Branch[-24])

    assertEquals("亥", 亥.toString())
    assertEquals(亥, Branch['亥'])
    assertSame(亥, Branch['亥'])
    assertEquals(亥, Branch[11])
    assertSame(亥, Branch[11])
    assertSame(亥, Branch[23])
    assertSame(亥, Branch[-1])
    assertSame(亥, Branch[-13])
  }

  @Test
  fun trilogyTest() {
    assertEquals(水, 申.trilogy())
    assertEquals(水, 子.trilogy())
    assertEquals(水, 辰.trilogy())

    assertEquals(木, 亥.trilogy())
    assertEquals(木, 卯.trilogy())
    assertEquals(木, 未.trilogy())

    assertEquals(金, 巳.trilogy())
    assertEquals(金, 酉.trilogy())
    assertEquals(金, 丑.trilogy())

    assertEquals(火, 寅.trilogy())
    assertEquals(火, 午.trilogy())
    assertEquals(火, 戌.trilogy())
  }

  @Test
  fun trilogyCountTest() {
    assertEquals(0, 子.trilogyCount(丑, 寅, 卯, 巳, 午, 未, 酉, 戌, 亥))
    assertEquals(2, 子.trilogyCount(申, 辰))
    assertEquals(3, 子.trilogyCount(申, 子, 辰))
    assertEquals(4, 子.trilogyCount(申, 申, 辰, 辰))
    assertEquals(6, 子.trilogyCount(申, 申, 辰, 辰, 子, 子))
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
    assertEquals(水, 亥.direction())
    assertEquals(水, 子.direction())
    assertEquals(水, 丑.direction())

    assertEquals(木, 寅.direction())
    assertEquals(木, 卯.direction())
    assertEquals(木, 辰.direction())

    assertEquals(火, 巳.direction())
    assertEquals(火, 午.direction())
    assertEquals(火, 未.direction())

    assertEquals(金, 申.direction())
    assertEquals(金, 酉.direction())
    assertEquals(金, 戌.direction())
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

