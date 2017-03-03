/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:10:50
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.IsoEra;

public class TimeDecoratorEnglishTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testGetOutputString() {
    TimeDecoratorEnglish decorator = new TimeDecoratorEnglish();

    LocalDateTime time;
    time = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    logger.info("{} : {}" , time , decorator.getOutputString(time));


    time = LocalDateTime.of(LocalDate.of(2000 , 12 , 31).with(IsoEra.BCE) , LocalTime.of(23 , 59 , 59 , 999_000_000));
    logger.info("{} : {}" , time , decorator.getOutputString(time));
  }

}
