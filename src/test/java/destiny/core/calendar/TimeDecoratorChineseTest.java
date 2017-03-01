/**
 * @author smallufo
 * Created on 2007/3/20 at 上午 6:39:27
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TimeDecoratorChineseTest {

  private Logger logger = LoggerFactory.getLogger(getClass());


  @Test
  public void testGetOutputString() {
    TimeDecoratorChinese decorator = new TimeDecoratorChinese();
    LdtDecoratorChinese decorator2 = new LdtDecoratorChinese();

    Time time = new Time(true, 2000, 1, 1, 0, 0, 0);
    assertNotNull(decorator.getOutputString(time));
    logger.info("1 : {}" , decorator.getOutputString(time));
    logger.info("2 : {}" , decorator2.getOutputString(time.toLocalDateTime()));

    time = new Time(false, 2000, 12, 31, 23, 59, 59.999);
    assertNotNull(decorator.getOutputString(time));
    logger.info("3 : {}" , decorator.getOutputString(time));
    logger.info("4 : {}" , decorator2.getOutputString(time.toLocalDateTime()));
  }

  @Test
  public void testDebugString() {
    Time time = new Time(true, 2000, 1, 1, 0, 0, 0);

    assertEquals(time.getDebugString() , Time.getDebugString(time.toLocalDateTime()));

    time = new Time(false, 2000, 12, 31, 23, 59, 59.999);
    assertEquals(time.getDebugString() , Time.getDebugString(time.toLocalDateTime()));

  }

}
