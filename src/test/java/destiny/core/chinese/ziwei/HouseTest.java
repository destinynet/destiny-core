/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

public class HouseTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(House house : House.values()) {
      assertNotNull(house.toString());
      logger.info("{}" , house.toString());
    }
  }

}