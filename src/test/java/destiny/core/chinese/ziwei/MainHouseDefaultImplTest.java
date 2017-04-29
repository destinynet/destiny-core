/**
 * Created by smallufo on 2017-04-29.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.junit.Assert.*;

public class MainHouseDefaultImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private IMainHouse impl = new MainHouseDefaultImpl();

  @Test
  public void testTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN));
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE));

    logger.info("tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

}