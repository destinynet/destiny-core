/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.iching

import destiny.core.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class SymbolAcquiredTest {

  @Test
  fun testGetIndex() {
    assertEquals(4, SymbolAcquired.getIndex(巽).toLong())
    assertEquals(3, SymbolAcquired.getIndex(震).toLong())
    assertEquals(8, SymbolAcquired.getIndex(艮).toLong())
    assertEquals(9, SymbolAcquired.getIndex(離).toLong())

    assertEquals(1, SymbolAcquired.getIndex(坎).toLong())
    assertEquals(2, SymbolAcquired.getIndex(坤).toLong())
    assertEquals(7, SymbolAcquired.getIndex(兌).toLong())
    assertEquals(6, SymbolAcquired.getIndex(乾).toLong())
  }

  /**
   * [SymbolAcquired.getSymbol] 是 [SymbolAcquired.getSymbolNullable] 的**週期版**：
   * 先對 9 取餘，故 0 等同 9（離）、10 等同 1（坎），不像 nullable 版超過 9 就爆掉。
   * 中宮（5）沒有對應的卦，回傳 null。
   *
   * ```
   * 巽4 | 離9 | 坤2
   * ----+-----+----
   * 震3 |  5  | 兌7
   * ----+-----+----
   * 艮8 | 坎1 | 乾6
   * ```
   *
   * 原本這裡只是把 0..19 印出來用眼睛核對。
   */
  @Test
  fun testGetSymbol() {
    val oneToNine = listOf(坎, 坤, 震, 巽, null, 乾, 兌, 艮, 離)

    // 1..9 為洛書本盤
    assertEquals(oneToNine, (1..9).map { SymbolAcquired.getSymbol(it) })
    // 0 ≡ 9 (離)
    assertSame(離, SymbolAcquired.getSymbol(0))
    // 10.. 起以 9 為週期重複
    assertEquals(oneToNine, (10..18).map { SymbolAcquired.getSymbol(it) })
    assertSame(坎, SymbolAcquired.getSymbol(19))

    // 1..9 的範圍內，與 getSymbolNullable 完全一致
    (1..9).forEach { i ->
      assertEquals(SymbolAcquired.getSymbolNullable(i), SymbolAcquired.getSymbol(i), "index = $i")
    }
  }

  @Test
  fun testGetSymbolNullable() {
    assertEquals(坎, SymbolAcquired.getSymbolNullable(1))
    assertEquals(坤, SymbolAcquired.getSymbolNullable(2))
    assertEquals(震, SymbolAcquired.getSymbolNullable(3))
    assertEquals(巽, SymbolAcquired.getSymbolNullable(4))

    assertNull(SymbolAcquired.getSymbolNullable(5))

    assertEquals(乾, SymbolAcquired.getSymbolNullable(6))
    assertEquals(兌, SymbolAcquired.getSymbolNullable(7))
    assertEquals(艮, SymbolAcquired.getSymbolNullable(8))
    assertEquals(離, SymbolAcquired.getSymbolNullable(9))
  }

  @Test
  fun getOppositeSymbol() {
    assertSame(離 , SymbolAcquired.getOppositeSymbol(坎))
    assertSame(艮 , SymbolAcquired.getOppositeSymbol(坤))
    assertSame(兌 , SymbolAcquired.getOppositeSymbol(震))
    assertSame(乾 , SymbolAcquired.getOppositeSymbol(巽))
    assertSame(巽 , SymbolAcquired.getOppositeSymbol(乾))
    assertSame(震 , SymbolAcquired.getOppositeSymbol(兌))
    assertSame(坤 , SymbolAcquired.getOppositeSymbol(艮))
    assertSame(坎 , SymbolAcquired.getOppositeSymbol(離))
  }
}
