/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.iching.Symbol.*;
import static org.junit.Assert.assertSame;

public class StarSymbolDefaultImplTest {

  @Test
  public void getSymbolAcquired() {
    IStarSymbol impl = new StarSymbolDefaultImpl();

    assertSame(艮, impl.getSymbolAcquired(Companion.get紫微()));
    assertSame(艮, impl.getSymbolAcquired(Companion.get天府()));
    assertSame(震, impl.getSymbolAcquired(Companion.get天機()));
    assertSame(震, impl.getSymbolAcquired(Companion.get巨門()));
    assertSame(巽, impl.getSymbolAcquired(Companion.get貪狼()));
    assertSame(離, impl.getSymbolAcquired(Companion.get太陽()));
    assertSame(離, impl.getSymbolAcquired(Companion.get天相()));
    assertSame(坤, impl.getSymbolAcquired(Companion.get武曲()));
    assertSame(坤, impl.getSymbolAcquired(Companion.get破軍()));
    assertSame(兌, impl.getSymbolAcquired(Companion.get天同()));
    assertSame(兌, impl.getSymbolAcquired(Companion.get天梁()));
    assertSame(乾, impl.getSymbolAcquired(Companion.get七殺()));
    assertSame(坎, impl.getSymbolAcquired(Companion.get廉貞()));
    assertSame(坎, impl.getSymbolAcquired(Companion.get太陰()));


  }

}