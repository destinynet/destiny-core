/**
 * Created by smallufo on 2018-03-08.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarMainJavaTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void list() {
    for (StarMain starMain : StarMain.Companion.getValues()) {
      logger.info("star = {}" , starMain);
    }
  }
}
