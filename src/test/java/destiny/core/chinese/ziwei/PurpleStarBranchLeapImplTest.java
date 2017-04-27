/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.Branch.*;
import static org.junit.Assert.assertEquals;

public class PurpleStarBranchLeapImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void water2() throws Exception {
    PurpleStarBranchLeapImpl impl = new PurpleStarBranchLeapImpl();
    assertEquals(巳, impl.water2(32));
    assertEquals(巳, impl.water2(33));

    assertEquals(申, impl.water2(38));
    assertEquals(申, impl.water2(39));

    assertEquals(丑, impl.water2(48));
    assertEquals(丑, impl.water2(49));

    assertEquals(午, impl.water2(58));
    assertEquals(午, impl.water2(59));

    assertEquals(未, impl.water2(60));
  }

}