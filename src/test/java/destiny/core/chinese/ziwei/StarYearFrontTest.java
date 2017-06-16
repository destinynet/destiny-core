/**
 * Created by smallufo on 2017-06-17.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Branch.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class StarYearFrontTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(StarYearFront star : StarYearFront.values) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }

  /**
   * 比對 http://blog.xuite.net/paulwang0129/twblog/164106473
   */
  @Test
  public void testFun() {
    // 午年 , 歲建 在 午
    assertSame(午 , StarYearFront.fun歲建.apply(午));
    // 午年 , 晦氣 在 未
    assertSame(未 , StarYearFront.fun晦氣.apply(午));
    // 午年 , 歲破(大耗) 在 子
    assertSame(子 , StarYearFront.fun歲破.apply(午));
    // 午年 , 病符 在 巳
    assertSame(巳 , StarYearFront.fun病符.apply(午));
  }

}