/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:10:50
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TimeDecoratorEnglishTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testGetOutputString() {
    TimeDecoratorEnglish decorator = new TimeDecoratorEnglish();
    LdtDecoratorEnglish decorator2 = new LdtDecoratorEnglish();

    Time time;
    time = new Time(true, 2000, 1, 1, 0, 0, 0);
    assertNotNull(decorator.getOutputString(time));
    logger.info("1 : {}" , decorator.getOutputString(time));
    logger.info("2 : {}" , decorator2.getOutputString(time.toLocalDateTime()));
    assertEquals(decorator.getOutputString(time) , decorator2.getOutputString(time.toLocalDateTime()));


    time = new Time(false, 2000, 12, 31, 23, 59, 59.999);
    assertNotNull(decorator.getOutputString(time));
    logger.info("3 : {}" , decorator.getOutputString(time));
    logger.info("4 : {}" , decorator2.getOutputString(time.toLocalDateTime()));
    assertEquals(decorator.getOutputString(time) , decorator2.getOutputString(time.toLocalDateTime()));
  }

}
