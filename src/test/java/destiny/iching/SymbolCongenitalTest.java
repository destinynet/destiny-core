/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.iching;

import org.junit.Test;

public class SymbolCongenitalTest {

  @Test
  public void testGetSymbol() throws Exception {
    for(int i=1 ; i <=20 ; i++) {
      System.out.println("i = " + i + " , symbol = " + SymbolCongenital.getSymbol(i));
    }
  }
}