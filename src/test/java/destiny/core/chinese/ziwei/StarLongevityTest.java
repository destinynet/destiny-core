/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.Gender.女;
import static destiny.core.Gender.男;
import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.FiveElement.*;
import static destiny.core.chinese.YinYangIF.陰;
import static destiny.core.chinese.YinYangIF.陽;
import static destiny.core.chinese.ziwei.StarLongevity.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class StarLongevityTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(StarLongevity star : StarLongevity.VALUES) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }

  @Test
  public void testRun() {
    assertSame(申 , fun長生.apply(水 , 男 , 陽));
    assertSame(戌 , fun沐浴.apply(木 , 男 , 陰));
    assertSame(未 , fun冠帶.apply(金 , 女 , 陰));
    assertSame(午 ,   fun胎.apply(土 , 女 , 陰));
    assertSame(卯 ,   fun養.apply(火 , 男 , 陰));
  }
}
