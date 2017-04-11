/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

public class MinorStarTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for (MinorStar star : MinorStar.values) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }
}