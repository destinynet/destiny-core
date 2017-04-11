/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.UnluckyStar.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class UnluckyStarTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(UnluckyStar star : UnluckyStar.values) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }

  @Test
  public void test擎羊() {
    assertSame(卯 , fun擎羊.apply(甲));
    assertSame(辰 , fun擎羊.apply(乙));
    assertSame(午 , fun擎羊.apply(丙));
    assertSame(未 , fun擎羊.apply(丁));
    assertSame(午 , fun擎羊.apply(戊));
    assertSame(未 , fun擎羊.apply(己));
    assertSame(酉 , fun擎羊.apply(庚));
    assertSame(戌 , fun擎羊.apply(辛));
    assertSame(子 , fun擎羊.apply(壬));
    assertSame(丑 , fun擎羊.apply(癸));
  }

  @Test
  public void test陀羅() {
    assertSame(丑 , fun陀羅.apply(甲));
    assertSame(寅 , fun陀羅.apply(乙));
    assertSame(辰 , fun陀羅.apply(丙));
    assertSame(巳 , fun陀羅.apply(丁));
    assertSame(辰 , fun陀羅.apply(戊));
    assertSame(巳 , fun陀羅.apply(己));
    assertSame(未 , fun陀羅.apply(庚));
    assertSame(申 , fun陀羅.apply(辛));
    assertSame(戌 , fun陀羅.apply(壬));
    assertSame(亥 , fun陀羅.apply(癸));
  }

  @Test
  public void test火星() {
    assertSame(子 , fun火星.apply(午 , 亥));
    assertSame(卯 , fun火星.apply(申 , 丑));
    assertSame(辰 , fun火星.apply(丑 , 丑));
    assertSame(寅 , fun火星.apply(未 , 巳));
  }

  @Test
  public void test鈴星() {
    assertSame(丑 , fun鈴星.apply(戌 , 戌));
    assertSame(午 , fun鈴星.apply(申 , 申));
    assertSame(卯 , fun鈴星.apply(巳 , 巳));
    assertSame(丑 , fun鈴星.apply(卯 , 卯));
  }
}