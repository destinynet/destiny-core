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

public class LunarNodeTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(LunarNode each : LunarNode.values) {
      assertNotNull(each.toString());
      logger.info("{}" , each.toString());
    }

    Set<String> set = Arrays.stream(LunarNode.values).map(Point::toString).collect(Collectors.toSet());
    assertTrue(set.contains("北交點"));
    assertTrue(set.contains("南交點"));
  }
}