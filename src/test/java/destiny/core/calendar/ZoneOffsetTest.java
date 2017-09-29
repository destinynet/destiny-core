/**
 * Created by smallufo on 2017-09-29.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;

public class ZoneOffsetTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testZone() {
    ZoneOffset offset = ZoneOffset.of("+8");
    logger.info("offset = {}" , offset);
  }
}
