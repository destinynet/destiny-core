/**
 * @author smallufo
 * Created on 2010/6/23 at 下午7:32:33
 */
package destiny.iching

import org.junit.Assert.*
import kotlin.test.Test

class HexagramTest {

  @Test
  fun testGetLine() {
    assertTrue(Hexagram.乾.getLine(1))
    assertTrue(Hexagram.乾.getLine(2))
    assertTrue(Hexagram.乾.getLine(3))
    assertTrue(Hexagram.乾.getLine(4))
    assertTrue(Hexagram.乾.getLine(5))
    assertTrue(Hexagram.乾.getLine(6))

    assertFalse(Hexagram.坤.getLine(1))
    assertFalse(Hexagram.坤.getLine(2))
    assertFalse(Hexagram.坤.getLine(3))
    assertFalse(Hexagram.坤.getLine(4))
    assertFalse(Hexagram.坤.getLine(5))
    assertFalse(Hexagram.坤.getLine(6))
  }

  @Test
  fun testBinaryCode() {
    assertEquals("111111" , Hexagram.乾.binaryCode)
    assertEquals("000000" , Hexagram.坤.binaryCode)
    assertEquals("100010" , Hexagram.屯.binaryCode)
    assertEquals("010001" , Hexagram.蒙.binaryCode)
    assertEquals("010101" , Hexagram.未濟.binaryCode)
    assertEquals("101010" , Hexagram.既濟.binaryCode)
  }

  @Test
  fun testGetTuple() {
    var list: List<Int>
    var pair: Pair<IHexagram, IHexagram>
    list = listOf(7, 7, 7, 7, 7, 7)
    pair = Hexagram.getHexagrams(list)
    assertEquals(Hexagram.乾, pair.first)
    assertEquals(Hexagram.乾, pair.second)

    list = listOf(9, 9, 9, 9, 9, 9)
    pair = Hexagram.getHexagrams(list)
    assertEquals(Hexagram.乾, pair.first)
    assertEquals(Hexagram.坤, pair.second)

    list = listOf(9, 9, 9, 6, 6, 6)
    pair = Hexagram.getHexagrams(list)
    assertEquals(Hexagram.泰, pair.first)
    assertEquals(Hexagram.否, pair.second)

    list = listOf(6, 6, 6, 9, 9, 9)
    pair = Hexagram.getHexagrams(list)
    assertEquals(Hexagram.否, pair.first)
    assertEquals(Hexagram.泰, pair.second)

    list = listOf(6, 9, 6, 9, 6, 9)
    pair = Hexagram.getHexagrams(list)
    assertEquals(Hexagram.未濟, pair.first)
    assertEquals(Hexagram.既濟, pair.second)

    list = listOf(9, 6, 9, 6, 9, 6)
    pair = Hexagram.getHexagrams(list)
    assertEquals(Hexagram.既濟, pair.first)
    assertEquals(Hexagram.未濟, pair.second)
  }

  @Test
  fun testGetHexagramLine() {
    val src = Hexagram.乾
    assertSame(Hexagram.姤, src.getHexagram(1))
  }

  @Test
  fun testHexagram() {
    val set = mutableSetOf<Hexagram>()

    for (h in Hexagram.values()) {
      assertTrue(!set.contains(h))
      set.add(h)
      assertNotNull(h)

      assertNotNull(h.upperSymbol)
      assertNotNull(h.lowerSymbol)
    }
    assertSame(64, set.size)
  }

  /**
   * 測試 hexagram with 多個動爻
   */
  @Test
  fun testHexagramWithMotivLines() {
    assertEquals(Hexagram.姤, Hexagram.乾.getHexagram(1))

    assertEquals(Hexagram.遯, Hexagram.乾.getHexagram(1, 2))
    assertEquals(Hexagram.遯, Hexagram.乾.getHexagram(2, 1))

    assertEquals(Hexagram.否, Hexagram.乾.getHexagram(1, 2, 3))
    assertEquals(Hexagram.否, Hexagram.乾.getHexagram(3, 2, 1))
    assertEquals(Hexagram.否, Hexagram.乾.getHexagram(2, 3, 1))

    assertEquals(Hexagram.觀, Hexagram.乾.getHexagram(1, 2, 3, 4))
    assertEquals(Hexagram.剝, Hexagram.乾.getHexagram(1, 2, 3, 4, 5))
    assertEquals(Hexagram.坤, Hexagram.乾.getHexagram(1, 2, 3, 4, 5, 6))

    assertEquals(Hexagram.復, Hexagram.坤.getHexagram(1))
    assertEquals(Hexagram.臨, Hexagram.坤.getHexagram(1, 2))
    assertEquals(Hexagram.泰, Hexagram.坤.getHexagram(1, 2, 3))
    assertEquals(Hexagram.大壯, Hexagram.坤.getHexagram(1, 2, 3, 4))
    assertEquals(Hexagram.夬, Hexagram.坤.getHexagram(1, 2, 3, 4, 5))
    assertEquals(Hexagram.乾, Hexagram.坤.getHexagram(1, 2, 3, 4, 5, 6))
  }

  /** 測試互卦  */
  @Test
  fun testMiddleSpanHexagram() {
    assertSame(Hexagram.蹇, Hexagram.晉.middleSpanHexagram)
  }

  /** 測試錯卦  */
  @Test
  fun testInterlacedHexagram() {
    assertSame(Hexagram.需, Hexagram.晉.interlacedHexagram)
  }

  /** 測試綜卦  */
  @Test
  fun testReversedHexagram() {
    assertSame(Hexagram.明夷, Hexagram.晉.reversedHexagram)
  }
}
