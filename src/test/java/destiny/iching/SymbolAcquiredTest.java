/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.iching;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymbolAcquiredTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testGetSymbol() throws Exception {
    for(int i=0 ; i < 20 ; i++) {
      logger.info("index = {} , 後天卦 = {}" , i , SymbolAcquired.getSymbol(i));
    }
  }

  @Test
  public void testGetSymbolNullable() throws Exception {
    for(int i=1 ; i <= 9 ; i++) {
      logger.info("index = {} , 後天卦 = {}" , i , SymbolAcquired.getSymbolNullable(i));
    }
  }
}