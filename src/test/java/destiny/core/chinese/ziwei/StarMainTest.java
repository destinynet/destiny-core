/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

public class StarMainTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(StarMain star : StarMain.Companion.getValues()) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }
}