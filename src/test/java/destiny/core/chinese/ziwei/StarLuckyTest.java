/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.TianyiIF;
import destiny.core.chinese.impls.TianyiZiweiBookImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.StarLucky.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class StarLuckyTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(StarLucky star : StarLucky.values) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }

  @Test
  public void test文昌() {
    assertSame(戌 , fun文昌.apply(子));
    assertSame(酉 , fun文昌.apply(丑));
    assertSame(申 , fun文昌.apply(寅));
    assertSame(未 , fun文昌.apply(卯));
    assertSame(午 , fun文昌.apply(辰));
    assertSame(巳 , fun文昌.apply(巳));
    assertSame(辰 , fun文昌.apply(午));
    assertSame(卯 , fun文昌.apply(未));
    assertSame(寅 , fun文昌.apply(申));
    assertSame(丑 , fun文昌.apply(酉));
    assertSame(子 , fun文昌.apply(戌));
    assertSame(亥 , fun文昌.apply(亥));
  }

  @Test
  public void test文曲() {
    assertSame(辰 , fun文曲.apply(子));
    assertSame(巳 , fun文曲.apply(丑));
    assertSame(午 , fun文曲.apply(寅));
    assertSame(未 , fun文曲.apply(卯));
    assertSame(申 , fun文曲.apply(辰));
    assertSame(酉 , fun文曲.apply(巳));
    assertSame(戌 , fun文曲.apply(午));
    assertSame(亥 , fun文曲.apply(未));
    assertSame(子 , fun文曲.apply(申));
    assertSame(丑 , fun文曲.apply(酉));
    assertSame(寅 , fun文曲.apply(戌));
    assertSame(卯 , fun文曲.apply(亥));
  }

  @Test
  public void test左輔() {
    assertSame(辰 , fun左輔_月數.apply(1));
    assertSame(巳 , fun左輔_月數.apply(2));
    assertSame(午 , fun左輔_月數.apply(3));
    assertSame(未 , fun左輔_月數.apply(4));
    assertSame(申 , fun左輔_月數.apply(5));
    assertSame(酉 , fun左輔_月數.apply(6));
    assertSame(戌 , fun左輔_月數.apply(7));
    assertSame(亥 , fun左輔_月數.apply(8));
    assertSame(子 , fun左輔_月數.apply(9));
    assertSame(丑 , fun左輔_月數.apply(10));
    assertSame(寅 , fun左輔_月數.apply(11));
    assertSame(卯 , fun左輔_月數.apply(12));
  }

  @Test
  public void test右弼() {
    assertSame(戌 , fun右弼_月數.apply(1));
    assertSame(酉 , fun右弼_月數.apply(2));
    assertSame(申 , fun右弼_月數.apply(3));
    assertSame(未 , fun右弼_月數.apply(4));
    assertSame(午 , fun右弼_月數.apply(5));
    assertSame(巳 , fun右弼_月數.apply(6));
    assertSame(辰 , fun右弼_月數.apply(7));
    assertSame(卯 , fun右弼_月數.apply(8));
    assertSame(寅 , fun右弼_月數.apply(9));
    assertSame(丑 , fun右弼_月數.apply(10));
    assertSame(子 , fun右弼_月數.apply(11));
    assertSame(亥 , fun右弼_月數.apply(12));
  }

  @Test
  public void test天魁() {
    TianyiIF tianyiImpl = new TianyiZiweiBookImpl();
    assertSame(丑 , fun天魁.apply(甲 , tianyiImpl));
    assertSame(子 , fun天魁.apply(乙 , tianyiImpl));
    assertSame(亥 , fun天魁.apply(丙 , tianyiImpl));
    assertSame(亥 , fun天魁.apply(丁 , tianyiImpl));
    assertSame(丑 , fun天魁.apply(戊 , tianyiImpl));
    assertSame(子 , fun天魁.apply(己 , tianyiImpl));
    assertSame(丑 , fun天魁.apply(庚 , tianyiImpl));
    assertSame(午 , fun天魁.apply(辛 , tianyiImpl));
    assertSame(卯 , fun天魁.apply(壬 , tianyiImpl));
    assertSame(卯 , fun天魁.apply(癸 , tianyiImpl));
  }

  @Test
  public void test天鉞() {
    TianyiIF tianyiImpl = new TianyiZiweiBookImpl();
    assertSame(未 , fun天鉞.apply(甲 , tianyiImpl));
    assertSame(申 , fun天鉞.apply(乙 , tianyiImpl));
    assertSame(酉 , fun天鉞.apply(丙 , tianyiImpl));
    assertSame(酉 , fun天鉞.apply(丁 , tianyiImpl));
    assertSame(未 , fun天鉞.apply(戊 , tianyiImpl));
    assertSame(申 , fun天鉞.apply(己 , tianyiImpl));
    assertSame(未 , fun天鉞.apply(庚 , tianyiImpl));
    assertSame(寅 , fun天鉞.apply(辛 , tianyiImpl));
    assertSame(巳 , fun天鉞.apply(壬 , tianyiImpl));
    assertSame(巳 , fun天鉞.apply(癸 , tianyiImpl));
  }

  @Test
  public void test祿存() {
    assertSame(寅 , fun祿存.apply(甲));
    assertSame(卯 , fun祿存.apply(乙));
    assertSame(巳 , fun祿存.apply(丙));
    assertSame(午 , fun祿存.apply(丁));
    assertSame(巳 , fun祿存.apply(戊));
    assertSame(午 , fun祿存.apply(己));
    assertSame(申 , fun祿存.apply(庚));
    assertSame(酉 , fun祿存.apply(辛));
    assertSame(亥 , fun祿存.apply(壬));
    assertSame(子 , fun祿存.apply(癸));
  }

  @Test
  public void test天馬() {
    assertSame(寅 , fun年馬_年支.apply(子));
    assertSame(亥 , fun年馬_年支.apply(丑));
    assertSame(申 , fun年馬_年支.apply(寅));
    assertSame(巳 , fun年馬_年支.apply(卯));
    assertSame(寅 , fun年馬_年支.apply(辰));
    assertSame(亥 , fun年馬_年支.apply(巳));
    assertSame(申 , fun年馬_年支.apply(午));
    assertSame(巳 , fun年馬_年支.apply(未));
    assertSame(寅 , fun年馬_年支.apply(申));
    assertSame(亥 , fun年馬_年支.apply(酉));
    assertSame(申 , fun年馬_年支.apply(戌));
    assertSame(巳 , fun年馬_年支.apply(亥));

    assertSame(寅 , fun月馬_月數.apply(11));
    assertSame(亥 , fun月馬_月數.apply(12));
    assertSame(申 , fun月馬_月數.apply(1));
    assertSame(巳 , fun月馬_月數.apply(2));
    assertSame(寅 , fun月馬_月數.apply(3));
    assertSame(亥 , fun月馬_月數.apply(4));
    assertSame(申 , fun月馬_月數.apply(5));
    assertSame(巳 , fun月馬_月數.apply(6));
    assertSame(寅 , fun月馬_月數.apply(7));
    assertSame(亥 , fun月馬_月數.apply(8));
    assertSame(申 , fun月馬_月數.apply(9));
    assertSame(巳 , fun月馬_月數.apply(10));
  }
}