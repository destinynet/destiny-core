/**
 * Created by smallufo on 2017-06-26.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZContextTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testInit() throws IllegalAccessException, InstantiationException {
    Class clazz = ZContext.class;
    ZContext context = (ZContext) clazz.newInstance();
    logger.info("context = {}" , context);
  }

}