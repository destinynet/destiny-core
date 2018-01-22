/*
 * @author smallufo
 * @date 2004/11/20
 * @time 下午 05:43:54
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.Companion.甲子
import java.util.*
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

class StemBranchTest {

  @Test
  fun testList() {
    StemBranch.list.joinToString(",") { it.toString() }.let { println(it) }
  }


  @Test
  fun testEmpties() {
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 子)).contains(戌))
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 子)).contains(亥))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 酉)).contains(戌))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 酉)).contains(亥))
    assertTrue(StemBranch.get(甲, 子).empties.contains(戌))
    assertTrue(StemBranch.get(甲, 子).empties.contains(亥))
    assertTrue(StemBranch.get(癸, 酉).empties.contains(戌))
    assertTrue(StemBranch.get(癸, 酉).empties.contains(亥))

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 戌)).contains(申))
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 戌)).contains(酉))
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 午)).contains(申))
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 午)).contains(酉))
    assertTrue(StemBranch.get(甲, 戌).empties.contains(申))
    assertTrue(StemBranch.get(甲, 戌).empties.contains(酉))
    assertTrue(StemBranch.get(壬, 午).empties.contains(申))
    assertTrue(StemBranch.get(壬, 午).empties.contains(酉))

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 申)).contains(午))
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 申)).contains(未))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 巳)).contains(午))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 巳)).contains(未))
    assertTrue(StemBranch.get(甲, 申).empties.contains(午))
    assertTrue(StemBranch.get(甲, 申).empties.contains(未))
    assertTrue(StemBranch.get(癸, 巳).empties.contains(午))
    assertTrue(StemBranch.get(癸, 巳).empties.contains(未))

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 午)).contains(辰))
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 午)).contains(巳))
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 寅)).contains(辰))
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 寅)).contains(巳))
    assertTrue(StemBranch.get(甲, 午).empties.contains(辰))
    assertTrue(StemBranch.get(甲, 午).empties.contains(巳))
    assertTrue(StemBranch.get(壬, 寅).empties.contains(辰))
    assertTrue(StemBranch.get(壬, 寅).empties.contains(巳))

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 辰)).contains(寅))
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 辰)).contains(卯))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 丑)).contains(寅))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 丑)).contains(卯))
    assertTrue(StemBranch.get(甲, 辰).empties.contains(寅))
    assertTrue(StemBranch.get(甲, 辰).empties.contains(卯))
    assertTrue(StemBranch.get(癸, 丑).empties.contains(寅))
    assertTrue(StemBranch.get(癸, 丑).empties.contains(卯))

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 寅)).contains(子))
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 寅)).contains(丑))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 亥)).contains(子))
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 亥)).contains(丑))
    assertTrue(StemBranch.get(甲, 寅).empties.contains(子))
    assertTrue(StemBranch.get(甲, 寅).empties.contains(丑))
    assertTrue(StemBranch.get(癸, 亥).empties.contains(子))
    assertTrue(StemBranch.get(癸, 亥).empties.contains(丑))
  }


  @Test
  fun testGetAheadOf() {
    assertSame(0, 甲子.getAheadOf(甲子))
    assertSame(1, 甲子.getAheadOf(StemBranch.get("癸亥")))
    assertSame(59, 甲子.getAheadOf(StemBranch.get("乙丑")))

    assertSame(0, StemBranch.get("癸亥").getAheadOf(StemBranch.get("癸亥")))
    assertSame(1, StemBranch.get("癸亥").getAheadOf(StemBranch.get("壬戌")))
    assertSame(59, StemBranch.get("癸亥").getAheadOf(甲子))
  }

  @Test
  fun testNext() {
    assertSame(甲子, 甲子.next(0))
    assertSame(StemBranch.get("乙丑"), 甲子.next(1))
    assertSame(StemBranch.get("癸亥"), 甲子.next(59))
    assertSame(甲子, 甲子.next(60))
    assertSame(甲子, 甲子.next(600))

    assertSame(StemBranch.get("癸亥"), StemBranch.get("癸亥").next(0))
    assertSame(甲子, StemBranch.get("癸亥").next(1))
    assertSame(StemBranch.get("壬戌"), StemBranch.get("癸亥").next(59))
    assertSame(StemBranch.get("癸亥"), StemBranch.get("癸亥").next(60))
    assertSame(StemBranch.get("癸亥"), StemBranch.get("癸亥").next(600))
  }

  @Test
  fun testPrev() {
    assertSame(甲子, 甲子.prev(0))
    assertSame(StemBranch.get("癸亥"), 甲子.prev(1))
    assertSame(StemBranch.get("乙丑"), 甲子.prev(59))
    assertSame(甲子, 甲子.prev(60))
    assertSame(甲子, 甲子.prev(600))

    assertSame(StemBranch.get("癸亥"), StemBranch.get("癸亥").prev(0))
    assertSame(StemBranch.get("壬戌"), StemBranch.get("癸亥").prev(1))
    assertSame(甲子, StemBranch.get("癸亥").prev(59))
    assertSame(StemBranch.get("癸亥"), StemBranch.get("癸亥").prev(60))
    assertSame(StemBranch.get("癸亥"), StemBranch.get("癸亥").prev(600))
  }


  @Test
  fun testGet() {
    assertSame(甲子, StemBranch.get(甲, 子))
    assertSame(StemBranch.get("乙丑"), StemBranch.get(乙, 丑))
    assertSame(StemBranch.get("丙寅"), StemBranch.get(丙, 寅))
    assertSame(StemBranch.get("丁卯"), StemBranch.get(丁, 卯))

    try {
      StemBranch.get(甲, 丑)
      fail()
    } catch (e: RuntimeException) {
      assertTrue(true)
    }

  }

  @Test
  fun testStemBranch() {
    val sb1 = StemBranch.get('甲', '子')

    val sb3 = StemBranch.get(0)
    assertSame(sb1, sb3)

    val sb4 = StemBranch.get(0)
    assertSame(sb1, sb4)

    assertSame(sb3, sb4)

    val sb5 = StemBranch.get(甲, 子)
    assertSame(sb4, sb5)

    val sb6 = StemBranch.get('甲', '子')
    assertSame(sb4, sb6)
    assertSame(sb5, sb6)

    val sb7 = 甲子
    assertSame(sb4, sb7)
    assertSame(sb5, sb7)
    assertSame(sb6, sb7)
  }

  @Test
  fun testSorting() {
    val SBArray1 = arrayOf(StemBranch.get(10), StemBranch.get(甲, 午), StemBranch.get(50), StemBranch.get('甲', '子'),
                           StemBranch.get(20))
    val expected =
      arrayOf(StemBranch.get('甲', '子'), StemBranch.get('甲', '戌'), StemBranch.get('甲', '申'), StemBranch.get('甲', '午'),
              StemBranch.get('甲', '寅'))
    Arrays.sort(SBArray1)
    assertTrue(Arrays.equals(expected, SBArray1))
  }

  @Test
  fun testGetNext() {
    var sb = 甲子
    assertSame(sb.next, StemBranch.get("乙丑"))

    sb = StemBranch.get("癸亥")
    assertSame(sb.next, 甲子)
  }

  @Test
  fun testGetPrevious() {
    var sb = 甲子
    assertSame(sb.previous, StemBranch.get("癸亥"))

    sb = StemBranch.get("乙丑")
    assertSame(sb.previous, 甲子)
  }
}
