/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.iching;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.iching.Symbol.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SymbolAcquiredTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testGetIndex() {
    assertEquals(4 ,SymbolAcquired.getIndex(巽));
    assertEquals(3 ,SymbolAcquired.getIndex(震));
    assertEquals(8 ,SymbolAcquired.getIndex(艮));
    assertEquals(9 ,SymbolAcquired.getIndex(離));

    assertEquals(1 ,SymbolAcquired.getIndex(坎));
    assertEquals(2 ,SymbolAcquired.getIndex(坤));
    assertEquals(7 ,SymbolAcquired.getIndex(兌));
    assertEquals(6 ,SymbolAcquired.getIndex(乾));
  }

  @Test
  public void testGetSymbol() throws Exception {
    for(int i=0 ; i < 20 ; i++) {
      logger.info("index = {} , 後天卦 = {}" , i , SymbolAcquired.getSymbol(i));
    }
  }

  @Test
  public void testGetSymbolNullable() throws Exception {
    assertEquals(坎 , SymbolAcquired.getSymbolNullable(1));
    assertEquals(坤 , SymbolAcquired.getSymbolNullable(2));
    assertEquals(震 , SymbolAcquired.getSymbolNullable(3));
    assertEquals(巽 , SymbolAcquired.getSymbolNullable(4));

    assertNull(SymbolAcquired.getSymbolNullable(5));

    assertEquals(乾 , SymbolAcquired.getSymbolNullable(6));
    assertEquals(兌 , SymbolAcquired.getSymbolNullable(7));
    assertEquals(艮 , SymbolAcquired.getSymbolNullable(8));
    assertEquals(離 , SymbolAcquired.getSymbolNullable(9));

  }
}