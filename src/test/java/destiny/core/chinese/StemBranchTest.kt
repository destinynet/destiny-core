/*
 * @author smallufo
 * @date 2004/11/20
 * @time 下午 05:43:54
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import java.util.*
import kotlin.test.*

class StemBranchTest {

  /** 兩種甲子目前不相等，未來要如何改，再想想 */
  @Test
  fun testEqualWithStemBranchOptional() {
    val a: IStemBranchOptional = StemBranch.甲子
    val b: IStemBranchOptional = StemBranchOptional[甲, 子]
    assertNotEquals(a, b)
  }

  @Test
  fun testList() {
    StemBranch.values().joinToString(",") { it.toString() }.let { println(it) }
  }

  @Test
  fun testGetCycle() {
    assertSame(StemBranchCycle.甲子, 甲子.cycle)
    assertSame(StemBranchCycle.甲子, 癸酉.cycle)

    assertSame(StemBranchCycle.甲戌, 甲戌.cycle)
    assertSame(StemBranchCycle.甲戌, 癸未.cycle)

    assertSame(StemBranchCycle.甲申, 甲申.cycle)
    assertSame(StemBranchCycle.甲申, 癸巳.cycle)

    assertSame(StemBranchCycle.甲午, 甲午.cycle)
    assertSame(StemBranchCycle.甲午, 癸卯.cycle)

    assertSame(StemBranchCycle.甲辰, 甲辰.cycle)
    assertSame(StemBranchCycle.甲辰, 癸丑.cycle)

    assertSame(StemBranchCycle.甲寅, 甲寅.cycle)
    assertSame(StemBranchCycle.甲寅, 癸亥.cycle)

  }


  @Test
  fun testIndex() {
    assertSame(0, StemBranch.甲子.index)
    assertSame(59, StemBranch.癸亥.index)
  }

  @Test
  fun testEmpties() {
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 子]).contains(戌))
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 子]).contains(亥))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 酉]).contains(戌))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 酉]).contains(亥))
    assertTrue(StemBranch[甲, 子].empties.contains(戌))
    assertTrue(StemBranch[甲, 子].empties.contains(亥))
    assertTrue(StemBranch[癸, 酉].empties.contains(戌))
    assertTrue(StemBranch[癸, 酉].empties.contains(亥))

    assertTrue(StemBranch.getEmpties(StemBranch[甲, 戌]).contains(申))
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 戌]).contains(酉))
    assertTrue(StemBranch.getEmpties(StemBranch[壬, 午]).contains(申))
    assertTrue(StemBranch.getEmpties(StemBranch[壬, 午]).contains(酉))
    assertTrue(StemBranch[甲, 戌].empties.contains(申))
    assertTrue(StemBranch[甲, 戌].empties.contains(酉))
    assertTrue(StemBranch[壬, 午].empties.contains(申))
    assertTrue(StemBranch[壬, 午].empties.contains(酉))

    assertTrue(StemBranch.getEmpties(StemBranch[甲, 申]).contains(午))
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 申]).contains(未))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 巳]).contains(午))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 巳]).contains(未))
    assertTrue(StemBranch[甲, 申].empties.contains(午))
    assertTrue(StemBranch[甲, 申].empties.contains(未))
    assertTrue(StemBranch[癸, 巳].empties.contains(午))
    assertTrue(StemBranch[癸, 巳].empties.contains(未))

    assertTrue(StemBranch.getEmpties(StemBranch[甲, 午]).contains(辰))
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 午]).contains(巳))
    assertTrue(StemBranch.getEmpties(StemBranch[壬, 寅]).contains(辰))
    assertTrue(StemBranch.getEmpties(StemBranch[壬, 寅]).contains(巳))
    assertTrue(StemBranch[甲, 午].empties.contains(辰))
    assertTrue(StemBranch[甲, 午].empties.contains(巳))
    assertTrue(StemBranch[壬, 寅].empties.contains(辰))
    assertTrue(StemBranch[壬, 寅].empties.contains(巳))

    assertTrue(StemBranch.getEmpties(StemBranch[甲, 辰]).contains(寅))
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 辰]).contains(卯))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 丑]).contains(寅))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 丑]).contains(卯))
    assertTrue(StemBranch[甲, 辰].empties.contains(寅))
    assertTrue(StemBranch[甲, 辰].empties.contains(卯))
    assertTrue(StemBranch[癸, 丑].empties.contains(寅))
    assertTrue(StemBranch[癸, 丑].empties.contains(卯))

    assertTrue(StemBranch.getEmpties(StemBranch[甲, 寅]).contains(子))
    assertTrue(StemBranch.getEmpties(StemBranch[甲, 寅]).contains(丑))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 亥]).contains(子))
    assertTrue(StemBranch.getEmpties(StemBranch[癸, 亥]).contains(丑))
    assertTrue(StemBranch[甲, 寅].empties.contains(子))
    assertTrue(StemBranch[甲, 寅].empties.contains(丑))
    assertTrue(StemBranch[癸, 亥].empties.contains(子))
    assertTrue(StemBranch[癸, 亥].empties.contains(丑))
  }


  @Test
  fun testGetAheadOf() {
    assertSame(0, 甲子.getAheadOf(甲子))
    assertSame(1, 甲子.getAheadOf(StemBranch["癸亥"]))
    assertSame(59, 甲子.getAheadOf(StemBranch["乙丑"]))

    assertSame(0, StemBranch.get("癸亥").getAheadOf(StemBranch.get("癸亥")))
    assertSame(1, StemBranch.get("癸亥").getAheadOf(StemBranch.get("壬戌")))
    assertSame(59, StemBranch.get("癸亥").getAheadOf(甲子))
  }

  @Test
  fun testNext() {
    assertSame(甲子, 甲子.next(0))
    assertSame(StemBranch["乙丑"], 甲子.next(1))
    assertSame(StemBranch["癸亥"], 甲子.next(59))
    assertSame(甲子, 甲子.next(60))
    assertSame(甲子, 甲子.next(600))

    assertSame(StemBranch["癸亥"], StemBranch["癸亥"].next(0))
    assertSame(甲子, StemBranch["癸亥"].next(1))
    assertSame(StemBranch["壬戌"], StemBranch["癸亥"].next(59))
    assertSame(StemBranch["癸亥"], StemBranch["癸亥"].next(60))
    assertSame(StemBranch["癸亥"], StemBranch["癸亥"].next(600))
  }

  @Test
  fun testPrev() {
    assertSame(甲子, 甲子.prev(0))
    assertSame(StemBranch["癸亥"], 甲子.prev(1))
    assertSame(StemBranch["乙丑"], 甲子.prev(59))
    assertSame(甲子, 甲子.prev(60))
    assertSame(甲子, 甲子.prev(600))

    assertSame(StemBranch["癸亥"], StemBranch["癸亥"].prev(0))
    assertSame(StemBranch["壬戌"], StemBranch["癸亥"].prev(1))
    assertSame(甲子, StemBranch["癸亥"].prev(59))
    assertSame(StemBranch["癸亥"], StemBranch["癸亥"].prev(60))
    assertSame(StemBranch["癸亥"], StemBranch["癸亥"].prev(600))
  }


  @Test
  fun testGet() {
    assertSame(甲子, StemBranch[甲, 子])
    assertSame(StemBranch["乙丑"], StemBranch[乙, 丑])
    assertSame(StemBranch["丙寅"], StemBranch[丙, 寅])
    assertSame(StemBranch["丁卯"], StemBranch[丁, 卯])

    try {
      StemBranch[甲, 丑]
      fail()
    } catch (e: RuntimeException) {
      assertTrue(true)
    }

  }

  @Test
  fun testStemBranch() {
    val sb1 = StemBranch['甲', '子']

    val sb3 = StemBranch[0]
    assertSame(sb1, sb3)

    val sb4 = StemBranch[0]
    assertSame(sb1, sb4)

    assertSame(sb3, sb4)

    val sb5 = StemBranch[甲, 子]
    assertSame(sb4, sb5)

    val sb6 = StemBranch['甲', '子']
    assertSame(sb4, sb6)
    assertSame(sb5, sb6)

    val sb7 = 甲子
    assertSame(sb4, sb7)
    assertSame(sb5, sb7)
    assertSame(sb6, sb7)
  }

  @Test
  fun testSorting() {
    val SBArray1 = arrayOf(StemBranch[10], StemBranch[甲, 午], StemBranch[50], StemBranch['甲', '子'], StemBranch[20])
    val expected =
      arrayOf(StemBranch['甲', '子'], StemBranch['甲', '戌'], StemBranch['甲', '申'], StemBranch['甲', '午'],
              StemBranch['甲', '寅'])
    Arrays.sort(SBArray1)
    assertTrue(Arrays.equals(expected, SBArray1))
  }

  @Test
  fun testGetNext() {
    var sb = 甲子
    assertSame(sb.next, StemBranch["乙丑"])

    sb = StemBranch["癸亥"]
    assertSame(sb.next, 甲子)
  }

  @Test
  fun testGetPrevious() {
    var sb = 甲子
    assertSame(sb.previous, StemBranch["癸亥"])

    sb = StemBranch["乙丑"]
    assertSame(sb.previous, 甲子)
  }
}
