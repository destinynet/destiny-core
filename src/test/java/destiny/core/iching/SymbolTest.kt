/*
 * @author smallufo
 * Created on 2005/1/19 at 下午 06:27:55
 */
package destiny.core.iching

import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import destiny.core.chinese.FiveElement.*
import destiny.core.iching.Symbol.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SymbolTest {
  /** 測試先天八卦排序  */
  @Test
  fun testSortingCongenital() {
    val s = arrayOf(兌, 艮, 坎, 乾, 離, 巽, 坤, 震)
    val c1 = SymbolCongenital()

    Arrays.sort(s, c1)

    val expected = arrayOf(乾, 兌, 離, 震, 巽, 坎, 艮, 坤)
    for (i in s.indices)
      assertEquals(expected[i], s[i])
  }

  /** 後天八卦排序  */
  @Test
  fun testSortingAcquired() {
    val s = arrayOf(兌, 艮, 坎, 乾, 離, 巽, 坤, 震)
    val c2 = SymbolAcquired()

    Arrays.sort(s, c2)

    val expected = arrayOf(坎, 坤, 震, 巽, 乾, 兌, 艮, 離)
    for (i in s.indices)
      assertEquals(expected[i], s[i])
  }

  /** 測試後天八卦順時針轉  */
  @Test
  fun testSymbolAcquiredClockwise() {
    /* 後天 離卦 順時針下一卦為 坤 */
    assertSame(坤, SymbolAcquired.getClockwiseSymbol(離))
    /* 後天 坤卦 順時針下一卦為 兌 */
    assertSame(兌, SymbolAcquired.getClockwiseSymbol(坤))
    /* 後天 兌卦 順時針下一卦為 乾 */
    assertSame(乾, SymbolAcquired.getClockwiseSymbol(兌))
    /* 後天 乾卦 順時針下一卦為 坎 */
    assertSame(坎, SymbolAcquired.getClockwiseSymbol(乾))
    /* 後天 坎卦 順時針下一卦為 艮 */
    assertSame(艮, SymbolAcquired.getClockwiseSymbol(坎))
    /* 後天 艮卦 順時針下一卦為 震 */
    assertSame(震, SymbolAcquired.getClockwiseSymbol(艮))
    /* 後天 震卦 順時針下一卦為 巽 */
    assertSame(巽, SymbolAcquired.getClockwiseSymbol(震))
    /* 後天 巽卦 順時針下一卦為 離 */
    assertSame(離, SymbolAcquired.getClockwiseSymbol(巽))
  }

  @Test
  fun testGetSymbol() {
    assertSame(乾, Symbol.getSymbol(true, true, true))
    assertSame(兌, Symbol.getSymbol(true, true, false))
    assertSame(離, Symbol.getSymbol(true, false, true))
    assertSame(震, Symbol.getSymbol(true, false, false))
    assertSame(巽, Symbol.getSymbol(false, true, true))
    assertSame(坎, Symbol.getSymbol(false, true, false))
    assertSame(艮, Symbol.getSymbol(false, false, true))
    assertSame(坤, Symbol.getSymbol(false, false, false))
  }

  @Test
  fun testGetSymbolFromIYinYang() {
    assertSame(乾, Symbol.getSymbol(arrayOf(DAY, DAY, DAY)))
    assertSame(兌, Symbol.getSymbol(arrayOf(DAY, DAY, NIGHT)))
    assertSame(離, Symbol.getSymbol(arrayOf(DAY, NIGHT, DAY)))
    assertSame(震, Symbol.getSymbol(arrayOf(DAY, NIGHT, NIGHT)))
    assertSame(巽, Symbol.getSymbol(arrayOf(NIGHT, DAY, DAY)))
    assertSame(坎, Symbol.getSymbol(arrayOf(NIGHT, DAY, NIGHT)))
    assertSame(艮, Symbol.getSymbol(arrayOf(NIGHT, NIGHT, DAY)))
    assertSame(坤, Symbol.getSymbol(arrayOf(NIGHT, NIGHT, NIGHT)))
  }

  @Test
  fun testGetFiveElement() {
    assertSame(金, 乾.fiveElement)
    assertSame(金, 兌.fiveElement)
    assertSame(火, 離.fiveElement)
    assertSame(木, 震.fiveElement)
    assertSame(木, 巽.fiveElement)
    assertSame(水, 坎.fiveElement)
    assertSame(土, 艮.fiveElement)
    assertSame(土, 坤.fiveElement)
  }

  @Test
  fun testGetName() {
    assertEquals("乾" , 乾.getName())
    assertEquals("兌" , 兌.getName())
    assertEquals("離" , 離.getName())
    assertEquals("震" , 震.getName())
    assertEquals("巽" , 巽.getName())
    assertEquals("坎" , 坎.getName())
    assertEquals("艮" , 艮.getName())
    assertEquals("坤" , 坤.getName())
  }


  @Test
  fun testToAcquired() {
    assertSame(離 , 乾.toAcquired())
    assertSame(巽 , 兌.toAcquired())
    assertSame(震 , 離.toAcquired())
    assertSame(艮 , 震.toAcquired())
    assertSame(坤 , 巽.toAcquired())
    assertSame(兌 , 坎.toAcquired())
    assertSame(乾 , 艮.toAcquired())
    assertSame(坎 , 坤.toAcquired())
  }

  @Test
  fun testToCongenital() {
    assertSame(艮 , 乾.toCongenital())
    assertSame(坎 , 兌.toCongenital())
    assertSame(乾 , 離.toCongenital())
    assertSame(離 , 震.toCongenital())
    assertSame(兌 , 巽.toCongenital())
    assertSame(坤 , 坎.toCongenital())
    assertSame(震 , 艮.toCongenital())
    assertSame(巽 , 坤.toCongenital())
  }

}
