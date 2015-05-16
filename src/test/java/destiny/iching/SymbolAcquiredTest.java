/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.iching;

import org.junit.Test;

public class SymbolAcquiredTest {

  @Test
  public void testGetSymbol() throws Exception {
    for(int i=0 ; i < 20 ; i++) {
      System.out.println("index = " + i + " , 後天卦 = " + SymbolAcquired.getSymbol(i));
    }
  }

  @Test
  public void testGetSymbolNullable() throws Exception {
    for(int i=1 ; i < 20 ; i++) {
      System.out.println("index = " + i + " , 後天卦 = " + SymbolAcquired.getSymbolNullable(i));
    }
  }
}