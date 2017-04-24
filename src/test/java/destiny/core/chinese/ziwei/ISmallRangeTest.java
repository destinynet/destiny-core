/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.Branch.酉;

public class ISmallRangeTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void getRanges() throws Exception {
    logger.info("{}" , ISmallRange.getRanges(Branch.申 , 酉 , Gender.男));
  }

}