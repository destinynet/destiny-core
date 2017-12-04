/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.StarMain.*
import destiny.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertSame

class StarSymbolDefaultImplTest {

  @Test
  fun getSymbolAcquired() {
    val impl = StarSymbolDefaultImpl()

    assertSame(艮, impl.getSymbolAcquired(紫微))
    assertSame(艮, impl.getSymbolAcquired(天府))
    assertSame(震, impl.getSymbolAcquired(天機))
    assertSame(震, impl.getSymbolAcquired(巨門))
    assertSame(巽, impl.getSymbolAcquired(貪狼))
    assertSame(離, impl.getSymbolAcquired(太陽))
    assertSame(離, impl.getSymbolAcquired(天相))
    assertSame(坤, impl.getSymbolAcquired(武曲))
    assertSame(坤, impl.getSymbolAcquired(破軍))
    assertSame(兌, impl.getSymbolAcquired(天同))
    assertSame(兌, impl.getSymbolAcquired(天梁))
    assertSame(乾, impl.getSymbolAcquired(七殺))
    assertSame(坎, impl.getSymbolAcquired(廉貞))
    assertSame(坎, impl.getSymbolAcquired(太陰))


  }

}