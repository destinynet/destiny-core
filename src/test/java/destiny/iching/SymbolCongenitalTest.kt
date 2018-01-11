/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.iching

import destiny.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertSame

class SymbolCongenitalTest {


  @Test
  fun getIndex() {
    assertSame(1 , SymbolCongenital.getIndex(乾))
    assertSame(2 , SymbolCongenital.getIndex(兌))
    assertSame(3 , SymbolCongenital.getIndex(離))
    assertSame(4 , SymbolCongenital.getIndex(震))
    assertSame(5 , SymbolCongenital.getIndex(巽))
    assertSame(6 , SymbolCongenital.getIndex(坎))
    assertSame(7 , SymbolCongenital.getIndex(艮))
    assertSame(8 , SymbolCongenital.getIndex(坤))
  }

  @Test
  fun testGetSymbol() {
    assertSame(乾, SymbolCongenital.getSymbol(-7))
    assertSame(兌, SymbolCongenital.getSymbol(-6))
    assertSame(離, SymbolCongenital.getSymbol(-5))
    assertSame(震, SymbolCongenital.getSymbol(-4))
    assertSame(巽, SymbolCongenital.getSymbol(-3))
    assertSame(坎, SymbolCongenital.getSymbol(-2))
    assertSame(艮, SymbolCongenital.getSymbol(-1))
    assertSame(坤, SymbolCongenital.getSymbol(0))

    assertSame(乾, SymbolCongenital.getSymbol(1))
    assertSame(兌, SymbolCongenital.getSymbol(2))
    assertSame(離, SymbolCongenital.getSymbol(3))
    assertSame(震, SymbolCongenital.getSymbol(4))
    assertSame(巽, SymbolCongenital.getSymbol(5))
    assertSame(坎, SymbolCongenital.getSymbol(6))
    assertSame(艮, SymbolCongenital.getSymbol(7))
    assertSame(坤, SymbolCongenital.getSymbol(8))

    assertSame(乾, SymbolCongenital.getSymbol(9))
    assertSame(兌, SymbolCongenital.getSymbol(10))
    assertSame(離, SymbolCongenital.getSymbol(11))
    assertSame(震, SymbolCongenital.getSymbol(12))
    assertSame(巽, SymbolCongenital.getSymbol(13))
    assertSame(坎, SymbolCongenital.getSymbol(14))
    assertSame(艮, SymbolCongenital.getSymbol(15))
    assertSame(坤, SymbolCongenital.getSymbol(16))
  }
}