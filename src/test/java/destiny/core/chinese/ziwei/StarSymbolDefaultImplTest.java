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

    assertSame(艮, impl.getSymbolAcquired(紫微.INSTANCE));
    assertSame(艮, impl.getSymbolAcquired(天府.INSTANCE));
    assertSame(震, impl.getSymbolAcquired(天機.INSTANCE));
    assertSame(震, impl.getSymbolAcquired(巨門.INSTANCE));
    assertSame(巽, impl.getSymbolAcquired(貪狼.INSTANCE));
    assertSame(離, impl.getSymbolAcquired(太陽.INSTANCE));
    assertSame(離, impl.getSymbolAcquired(天相.INSTANCE));
    assertSame(坤, impl.getSymbolAcquired(武曲.INSTANCE));
    assertSame(坤, impl.getSymbolAcquired(破軍.INSTANCE));
    assertSame(兌, impl.getSymbolAcquired(天同.INSTANCE));
    assertSame(兌, impl.getSymbolAcquired(天梁.INSTANCE));
    assertSame(乾, impl.getSymbolAcquired(七殺.INSTANCE));
    assertSame(坎, impl.getSymbolAcquired(廉貞.INSTANCE));
    assertSame(坎, impl.getSymbolAcquired(太陰.INSTANCE));


  }

}