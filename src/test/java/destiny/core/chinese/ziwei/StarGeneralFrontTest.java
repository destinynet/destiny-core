/**
 * Created by smallufo on 2017-06-16.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Branch.戌;
import static destiny.core.chinese.Branch.申;
import static destiny.core.chinese.Branch.酉;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class StarGeneralFrontTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(StarGeneralFront star : StarGeneralFront.values) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }

  @Test
  public void testFun() {
    // 酉年，將星[1] 在酉 , 攀鞍[2] 在戌 , 亡神[12] 在 申
    assertSame(酉 , StarGeneralFront.fun將星.apply(酉));
    assertSame(戌 , StarGeneralFront.fun攀鞍.apply(酉));
    assertSame(申 , StarGeneralFront.fun亡神.apply(申));
  }
}