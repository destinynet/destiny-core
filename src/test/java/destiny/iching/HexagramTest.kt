/**
 * @author smallufo
 * Created on 2010/6/23 at 下午7:32:33
 */
package destiny.iching

import destiny.iching.Hexagram.*
import kotlin.test.*

class HexagramTest {

  @Test
  fun testGetMotivatedLines() {
    assertTrue(乾.getMotivatedLines(乾).isEmpty())
    assertEquals(setOf(1), 乾.getMotivatedLines(姤))
    assertEquals(setOf(2), 乾.getMotivatedLines(同人))
    assertEquals(setOf(3), 乾.getMotivatedLines(履))
    assertEquals(setOf(4), 乾.getMotivatedLines(小畜))
    assertEquals(setOf(5), 乾.getMotivatedLines(大有))
    assertEquals(setOf(6), 乾.getMotivatedLines(夬))

    assertEquals(setOf(1, 2), 乾.getMotivatedLines(遯))
    assertEquals(setOf(2, 3), 乾.getMotivatedLines(無妄))
    assertEquals(setOf(3, 4), 乾.getMotivatedLines(中孚))
    assertEquals(setOf(4, 5), 乾.getMotivatedLines(大畜))
    assertEquals(setOf(5, 6), 乾.getMotivatedLines(大壯))

    assertEquals(setOf(1, 2, 3), 乾.getMotivatedLines(否))
    assertEquals(setOf(2, 3, 4), 乾.getMotivatedLines(益))
    assertEquals(setOf(3, 4, 5), 乾.getMotivatedLines(損))
    assertEquals(setOf(4, 5, 6), 乾.getMotivatedLines(泰))

    assertEquals(setOf(1, 2, 3, 4), 乾.getMotivatedLines(觀))
    assertEquals(setOf(2, 3, 4, 5), 乾.getMotivatedLines(頤))
    assertEquals(setOf(3, 4, 5, 6), 乾.getMotivatedLines(臨))

    assertEquals(setOf(1, 2, 3, 4, 5), 乾.getMotivatedLines(剝))
    assertEquals(setOf(2, 3, 4, 5, 6), 乾.getMotivatedLines(復))

    assertEquals(setOf(1, 2, 3, 4, 5, 6), 乾.getMotivatedLines(坤))
  }

  @Test
  fun testUnicode() {
    values().forEach { h ->
      assertNotNull(h.unicode)
      println("$h = ${h.unicode}")
    }
  }

  @Test
  fun testCongenitalOpposition() {
    assertSame(剝, 夬.congenitalOpposition)
    assertSame(夬, 剝.congenitalOpposition)

    assertSame(渙, 豐.congenitalOpposition)
    assertSame(豐, 渙.congenitalOpposition)

    assertSame(姤, 復.congenitalOpposition)
    assertSame(復, 姤.congenitalOpposition)

  }

  @Test
  fun testGetLine() {
    assertTrue(乾.getBoolean(1))
    assertTrue(乾.getBoolean(2))
    assertTrue(乾.getBoolean(3))
    assertTrue(乾.getBoolean(4))
    assertTrue(乾.getBoolean(5))
    assertTrue(乾.getBoolean(6))

    assertFalse(坤.getBoolean(1))
    assertFalse(坤.getBoolean(2))
    assertFalse(坤.getBoolean(3))
    assertFalse(坤.getBoolean(4))
    assertFalse(坤.getBoolean(5))
    assertFalse(坤.getBoolean(6))
  }

  @Test
  fun testGetUpperLowerSymbol() {
    assertSame(Symbol.離, 大有.upperSymbol)
    assertSame(Symbol.乾, 大有.lowerSymbol)

    assertSame(Symbol.震, 歸妹.upperSymbol)
    assertSame(Symbol.兌, 歸妹.lowerSymbol)

    assertSame(Symbol.坎, 既濟.upperSymbol)
    assertSame(Symbol.離, 既濟.lowerSymbol)
  }

  @Test
  fun testBinaryCode() {
    assertEquals("111111", 乾.binaryCode)
    assertEquals("000000", 坤.binaryCode)
    assertEquals("100010", 屯.binaryCode)
    assertEquals("010001", 蒙.binaryCode)
    assertEquals("010101", 未濟.binaryCode)
    assertEquals("101010", 既濟.binaryCode)
  }

  @Test
  fun testGetTuple() {

    Hexagram.getHexagrams(listOf(7, 7, 7, 7, 7, 7)).also { pair ->
      assertEquals(乾, pair.first)
      assertEquals(乾, pair.second)
    }

    Hexagram.getHexagrams(listOf(9, 9, 9, 9, 9, 9)).also { pair ->
      assertEquals(乾, pair.first)
      assertEquals(坤, pair.second)
    }

    Hexagram.getHexagrams(listOf(9, 9, 9, 6, 6, 6)).also { pair ->
      assertEquals(泰, pair.first)
      assertEquals(否, pair.second)
    }

    Hexagram.getHexagrams(listOf(6, 6, 6, 9, 9, 9)).also { pair ->
      assertEquals(否, pair.first)
      assertEquals(泰, pair.second)
    }

    Hexagram.getHexagrams(listOf(6, 9, 6, 9, 6, 9)).also { pair ->
      assertEquals(未濟, pair.first)
      assertEquals(既濟, pair.second)
    }

    Hexagram.getHexagrams(listOf(9, 6, 9, 6, 9, 6)).also { pair ->
      assertEquals(既濟, pair.first)
      assertEquals(未濟, pair.second)
    }

  }

  @Test
  fun testGetHexagramLine() {
    val src = 乾
    assertSame(姤, Hexagram.of(src.getTargetYinYangs(1)))
  }

  @Test
  fun testHexagram() {
    val set = mutableSetOf<Hexagram>()

    for (h in values()) {
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
    // line = 0 , 不變
    assertEquals(乾, Hexagram.of(乾.getTargetYinYangs(0)))

    assertEquals(姤, Hexagram.of(乾.getTargetYinYangs(1)))

    assertEquals(遯, Hexagram.of(乾.getTargetYinYangs(1, 2)))

    assertEquals(遯, Hexagram.of(乾.getTargetYinYangs(2, 1)))

    assertEquals(否, Hexagram.of(乾.getTargetYinYangs(1, 2, 3)))
    assertEquals(否, Hexagram.of(乾.getTargetYinYangs(3, 2, 1)))
    assertEquals(否, Hexagram.of(乾.getTargetYinYangs(2, 3, 1)))

    assertEquals(觀, Hexagram.of(乾.getTargetYinYangs(1, 2, 3, 4)))
    assertEquals(剝, Hexagram.of(乾.getTargetYinYangs(1, 2, 3, 4, 5)))
    assertEquals(坤, Hexagram.of(乾.getTargetYinYangs(1, 2, 3, 4, 5, 6)))

    assertEquals(復, Hexagram.of(坤.getTargetYinYangs(1)))
    assertEquals(臨, Hexagram.of(坤.getTargetYinYangs(1, 2)))
    assertEquals(泰, Hexagram.of(坤.getTargetYinYangs(1, 2, 3)))
    assertEquals(大壯, Hexagram.of(坤.getTargetYinYangs(1, 2, 3, 4)))
    assertEquals(夬, Hexagram.of(坤.getTargetYinYangs(1, 2, 3, 4, 5)))
    assertEquals(乾, Hexagram.of(坤.getTargetYinYangs(1, 2, 3, 4, 5, 6)))
  }

  /** 測試互卦  */
  @Test
  fun testMiddleSpanHexagram() {
    assertSame(蹇, 晉.middleSpanHexagram)
    assertSame(夬, 恆.middleSpanHexagram)
    assertSame(姤, 咸.middleSpanHexagram)
    assertSame(既濟, 歸妹.middleSpanHexagram)
    assertSame(未濟, 既濟.middleSpanHexagram)
  }

  /** 測試錯卦  */
  @Test
  fun testInterlacedHexagram() {
    assertSame(需, 晉.interlacedHexagram)
    assertSame(坤, 乾.interlacedHexagram)
    assertSame(未濟, 既濟.interlacedHexagram)
  }

  /** 測試綜卦  */
  @Test
  fun testReversedHexagram() {
    assertSame(明夷, 晉.reversedHexagram)
    assertSame(乾, 乾.reversedHexagram)
    assertSame(未濟, 既濟.reversedHexagram)
    assertSame(小過, 小過.reversedHexagram)
    assertSame(遯, 大壯.reversedHexagram)
  }
}
