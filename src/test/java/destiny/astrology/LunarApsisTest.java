/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.astrology;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LunarApsisTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(LunarApsis each : LunarApsis.values) {
      assertNotNull(each.toString());
      logger.info("{}" , each.toString());
    }

    Set<String> set = Arrays.stream(LunarApsis.values).map(Point::toString).collect(Collectors.toSet());
    assertTrue(set.contains("遠地點"));
    assertTrue(set.contains("近地點"));
  }

}