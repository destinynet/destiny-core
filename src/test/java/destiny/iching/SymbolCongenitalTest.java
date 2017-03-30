/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.iching;

import org.junit.Test;

import static destiny.iching.Symbol.*;
import static org.junit.Assert.assertSame;

public class SymbolCongenitalTest {

  @Test
  public void testGetSymbol() throws Exception {

    assertSame(乾 , SymbolCongenital.getSymbol(-7));
    assertSame(兌 , SymbolCongenital.getSymbol(-6));
    assertSame(離 , SymbolCongenital.getSymbol(-5));
    assertSame(震 , SymbolCongenital.getSymbol(-4));
    assertSame(巽 , SymbolCongenital.getSymbol(-3));
    assertSame(坎 , SymbolCongenital.getSymbol(-2));
    assertSame(艮 , SymbolCongenital.getSymbol(-1));
    assertSame(坤 , SymbolCongenital.getSymbol(0));

    assertSame(乾 , SymbolCongenital.getSymbol(1));
    assertSame(兌 , SymbolCongenital.getSymbol(2));
    assertSame(離 , SymbolCongenital.getSymbol(3));
    assertSame(震 , SymbolCongenital.getSymbol(4));
    assertSame(巽 , SymbolCongenital.getSymbol(5));
    assertSame(坎 , SymbolCongenital.getSymbol(6));
    assertSame(艮 , SymbolCongenital.getSymbol(7));
    assertSame(坤 , SymbolCongenital.getSymbol(8));

    assertSame(乾 , SymbolCongenital.getSymbol(9));
    assertSame(兌 , SymbolCongenital.getSymbol(10));
    assertSame(離 , SymbolCongenital.getSymbol(11));
    assertSame(震 , SymbolCongenital.getSymbol(12));
    assertSame(巽 , SymbolCongenital.getSymbol(13));
    assertSame(坎 , SymbolCongenital.getSymbol(14));
    assertSame(艮 , SymbolCongenital.getSymbol(15));
    assertSame(坤 , SymbolCongenital.getSymbol(16));
  }
}