/**
 * Created by smallufo on 2017-04-29.
 */
package destiny.core.chinese.impls;

import destiny.core.chinese.TianyiIF;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

@SuppressWarnings("Duplicates")
public class TianyiZiweiBookImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private TianyiIF impl = new TianyiZiweiBookImpl();

  @Test
  public void getTitle() throws Exception {
    assertNotNull(impl.getTitle(Locale.TAIWAN));
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE));
    assertNotNull(impl.getDescription(Locale.TAIWAN));
    assertNotNull(impl.getDescription(Locale.SIMPLIFIED_CHINESE));

    logger.info("tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.SIMPLIFIED_CHINESE));
    logger.info("tw = {} , cn = {}" , impl.getDescription(Locale.TAIWAN) , impl.getDescription(Locale.SIMPLIFIED_CHINESE));
  }

}