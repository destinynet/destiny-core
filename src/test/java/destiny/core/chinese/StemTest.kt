/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 06:48:01
 */
package destiny.core.chinese

import destiny.core.chinese.Stem.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue


class StemTest {

  @Test
  fun testGetAheadOf() {
    assertSame(0, 甲.getAheadOf(甲))
    assertSame(1, 甲.getAheadOf(癸))
    assertSame(2, 甲.getAheadOf(壬))
    assertSame(3, 甲.getAheadOf(辛))
    assertSame(4, 甲.getAheadOf(庚))
    assertSame(5, 甲.getAheadOf(己))
    assertSame(6, 甲.getAheadOf(戊))
    assertSame(7, 甲.getAheadOf(丁))
    assertSame(8, 甲.getAheadOf(丙))
    assertSame(9, 甲.getAheadOf(乙))


    assertSame(0, 癸.getAheadOf(癸))
    assertSame(1, 癸.getAheadOf(壬))
    assertSame(2, 癸.getAheadOf(辛))
    assertSame(3, 癸.getAheadOf(庚))
    assertSame(4, 癸.getAheadOf(己))
    assertSame(5, 癸.getAheadOf(戊))
    assertSame(6, 癸.getAheadOf(丁))
    assertSame(7, 癸.getAheadOf(丙))
    assertSame(8, 癸.getAheadOf(乙))
    assertSame(9, 癸.getAheadOf(甲))
  }

  @Test
  fun testNext() {
    assertSame(甲, 甲.next(0))
    assertSame(乙, 甲.next(1))
    assertSame(癸, 甲.next(9))
    assertSame(甲, 甲.next(10))
    assertSame(甲, 甲.next(100))

    assertSame(癸, 癸.next(0))
    assertSame(甲, 癸.next(1))
    assertSame(壬, 癸.next(9))
    assertSame(癸, 癸.next(10))
    assertSame(癸, 癸.next(100))
  }

  @Test
  fun testPrev() {
    assertSame(甲, 甲.prev(0))
    assertSame(癸, 甲.prev(1))
    assertSame(乙, 甲.prev(9))
    assertSame(甲, 甲.prev(10))
    assertSame(甲, 甲.prev(100))

    assertSame(癸, 癸.prev(0))
    assertSame(壬, 癸.prev(1))
    assertSame(甲, 癸.prev(9))
    assertSame(癸, 癸.prev(10))
    assertSame(癸, 癸.prev(100))
  }


  @Test
  fun testSorting() {
    val HSArray = arrayOf(丁, 癸, 甲, 戊, 辛)
    val expected = arrayOf(甲, 丁, 戊, 辛, 癸)

    Arrays.sort(HSArray)
    assertTrue(Arrays.equals(expected, HSArray))
  }

  @Test
  fun testValueOf() {
    assertEquals(甲, valueOf("甲"))
    assertSame(甲, valueOf("甲"))
    assertEquals(癸, valueOf("癸"))
  }

  @Test
  fun testGetYinYang() {
    assertEquals(true, 甲.booleanValue)
    assertEquals(false, 乙.booleanValue)
    assertEquals(true, 丙.booleanValue)
    assertEquals(false, 丁.booleanValue)
    assertEquals(true, 戊.booleanValue)
    assertEquals(false, 己.booleanValue)
    assertEquals(true, 庚.booleanValue)
    assertEquals(false, 辛.booleanValue)
    assertEquals(true, 壬.booleanValue)
    assertEquals(false, 癸.booleanValue)
  }

  @Test
  fun testGetFiveElements() {
    assertEquals(FiveElement.木, 甲.fiveElement)
    assertEquals(FiveElement.木, 乙.fiveElement)
    assertEquals(FiveElement.火, 丙.fiveElement)
    assertEquals(FiveElement.火, 丁.fiveElement)
    assertEquals(FiveElement.土, 戊.fiveElement)
    assertEquals(FiveElement.土, 己.fiveElement)
    assertEquals(FiveElement.金, 庚.fiveElement)
    assertEquals(FiveElement.金, 辛.fiveElement)
    assertEquals(FiveElement.水, 壬.fiveElement)
    assertEquals(FiveElement.水, 癸.fiveElement)
  }

  @Test
  fun testHeavenlyStems() {
    assertEquals(甲, Stem.get('甲'))
    assertSame(甲, Stem.get('甲'))


    assertSame(甲, Stem.get(-20))
    assertSame(甲, Stem.get(-10))
    assertSame(乙, Stem.get(-9))
    assertSame(壬, Stem.get(-2))
    assertSame(癸, Stem.get(-1))
    assertSame(甲, Stem.get(0))
    assertSame(乙, Stem.get(1))
    assertSame(丙, Stem.get(2))
    assertSame(丁, Stem.get(3))
    assertSame(戊, Stem.get(4))
    assertSame(己, Stem.get(5))
    assertSame(庚, Stem.get(6))
    assertSame(辛, Stem.get(7))
    assertSame(壬, Stem.get(8))
    assertSame(癸, Stem.get(9))
    assertSame(甲, Stem.get(10))
    assertSame(甲, Stem.get(20))


    assertEquals(癸, Stem.get('癸'))
    assertSame(癸, Stem.get('癸'))
    assertEquals(癸, Stem.get(9))
    assertEquals(癸, Stem.get(19))
    assertEquals(癸, Stem.get(-1))
    assertEquals(癸, Stem.get(-11))
  }

  @Test
  fun testGetHeavenlyStems() {
    assertSame(甲, Stem.get(FiveElement.木, true))
    assertSame(乙, Stem.get(FiveElement.木, false))
    assertSame(丙, Stem.get(FiveElement.火, true))
    assertSame(丁, Stem.get(FiveElement.火, false))
    assertSame(戊, Stem.get(FiveElement.土, true))
    assertSame(己, Stem.get(FiveElement.土, false))
    assertSame(庚, Stem.get(FiveElement.金, true))
    assertSame(辛, Stem.get(FiveElement.金, false))
    assertSame(壬, Stem.get(FiveElement.水, true))
    assertSame(癸, Stem.get(FiveElement.水, false))
  }

  @Test
  fun testSort() {
    val HSArray = arrayOf(丁, 癸, 甲, 戊, 辛)
    println("排序前:")

    for (aHSArray1 in HSArray) {
      print(aHSArray1.toString() + "\t")
    }

    Arrays.sort(HSArray)

    println("\n排序後")
    for (aHSArray in HSArray) {
      print(aHSArray.toString() + "\t")
    }
  }

  @Test
  fun _testHashCode() {
    println(甲.hashCode())
    println(乙.hashCode())
    println(丙.hashCode())
    println(丁.hashCode())
    println(戊.hashCode())
    println(己.hashCode())
    println(庚.hashCode())
    println(辛.hashCode())
    println(壬.hashCode())
    println(癸.hashCode())
  }
}
