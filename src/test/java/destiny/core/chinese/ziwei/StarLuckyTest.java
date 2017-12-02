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
    for(StarLucky star : StarLucky.Companion.getValues()) {
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
    assertSame(戌 , Companion.getFun文昌().invoke(子));
    assertSame(酉 , Companion.getFun文昌().invoke(丑));
    assertSame(申 , Companion.getFun文昌().invoke(寅));
    assertSame(未 , Companion.getFun文昌().invoke(卯));
    assertSame(午 , Companion.getFun文昌().invoke(辰));
    assertSame(巳 , Companion.getFun文昌().invoke(巳));
    assertSame(辰 , Companion.getFun文昌().invoke(午));
    assertSame(卯 , Companion.getFun文昌().invoke(未));
    assertSame(寅 , Companion.getFun文昌().invoke(申));
    assertSame(丑 , Companion.getFun文昌().invoke(酉));
    assertSame(子 , Companion.getFun文昌().invoke(戌));
    assertSame(亥 , Companion.getFun文昌().invoke(亥));
  }

  @Test
  public void test文曲() {
    assertSame(辰 , Companion.getFun文曲().invoke(子));
    assertSame(巳 , Companion.getFun文曲().invoke(丑));
    assertSame(午 , Companion.getFun文曲().invoke(寅));
    assertSame(未 , Companion.getFun文曲().invoke(卯));
    assertSame(申 , Companion.getFun文曲().invoke(辰));
    assertSame(酉 , Companion.getFun文曲().invoke(巳));
    assertSame(戌 , Companion.getFun文曲().invoke(午));
    assertSame(亥 , Companion.getFun文曲().invoke(未));
    assertSame(子 , Companion.getFun文曲().invoke(申));
    assertSame(丑 , Companion.getFun文曲().invoke(酉));
    assertSame(寅 , Companion.getFun文曲().invoke(戌));
    assertSame(卯 , Companion.getFun文曲().invoke(亥));
  }

  @Test
  public void test左輔() {
    assertSame(辰 , Companion.getFun左輔_月數().invoke(1));
    assertSame(巳 , Companion.getFun左輔_月數().invoke(2));
    assertSame(午 , Companion.getFun左輔_月數().invoke(3));
    assertSame(未 , Companion.getFun左輔_月數().invoke(4));
    assertSame(申 , Companion.getFun左輔_月數().invoke(5));
    assertSame(酉 , Companion.getFun左輔_月數().invoke(6));
    assertSame(戌 , Companion.getFun左輔_月數().invoke(7));
    assertSame(亥 , Companion.getFun左輔_月數().invoke(8));
    assertSame(子 , Companion.getFun左輔_月數().invoke(9));
    assertSame(丑 , Companion.getFun左輔_月數().invoke(10));
    assertSame(寅 , Companion.getFun左輔_月數().invoke(11));
    assertSame(卯 , Companion.getFun左輔_月數().invoke(12));
  }

  @Test
  public void test右弼() {
    assertSame(戌 , Companion.getFun右弼_月數().invoke(1));
    assertSame(酉 , Companion.getFun右弼_月數().invoke(2));
    assertSame(申 , Companion.getFun右弼_月數().invoke(3));
    assertSame(未 , Companion.getFun右弼_月數().invoke(4));
    assertSame(午 , Companion.getFun右弼_月數().invoke(5));
    assertSame(巳 , Companion.getFun右弼_月數().invoke(6));
    assertSame(辰 , Companion.getFun右弼_月數().invoke(7));
    assertSame(卯 , Companion.getFun右弼_月數().invoke(8));
    assertSame(寅 , Companion.getFun右弼_月數().invoke(9));
    assertSame(丑 , Companion.getFun右弼_月數().invoke(10));
    assertSame(子 , Companion.getFun右弼_月數().invoke(11));
    assertSame(亥 , Companion.getFun右弼_月數().invoke(12));
  }

  @Test
  public void test天魁() {
    TianyiIF tianyiImpl = new TianyiZiweiBookImpl();
    assertSame(丑 , Companion.getFun天魁().invoke(甲 , tianyiImpl));
    assertSame(子 , Companion.getFun天魁().invoke(乙 , tianyiImpl));
    assertSame(亥 , Companion.getFun天魁().invoke(丙 , tianyiImpl));
    assertSame(亥 , Companion.getFun天魁().invoke(丁 , tianyiImpl));
    assertSame(丑 , Companion.getFun天魁().invoke(戊 , tianyiImpl));
    assertSame(子 , Companion.getFun天魁().invoke(己 , tianyiImpl));
    assertSame(丑 , Companion.getFun天魁().invoke(庚 , tianyiImpl));
    assertSame(午 , Companion.getFun天魁().invoke(辛 , tianyiImpl));
    assertSame(卯 , Companion.getFun天魁().invoke(壬 , tianyiImpl));
    assertSame(卯 , Companion.getFun天魁().invoke(癸 , tianyiImpl));
  }

  @Test
  public void test天鉞() {
    TianyiIF tianyiImpl = new TianyiZiweiBookImpl();
    assertSame(未 , Companion.getFun天鉞().invoke(甲 , tianyiImpl));
    assertSame(申 , Companion.getFun天鉞().invoke(乙 , tianyiImpl));
    assertSame(酉 , Companion.getFun天鉞().invoke(丙 , tianyiImpl));
    assertSame(酉 , Companion.getFun天鉞().invoke(丁 , tianyiImpl));
    assertSame(未 , Companion.getFun天鉞().invoke(戊 , tianyiImpl));
    assertSame(申 , Companion.getFun天鉞().invoke(己 , tianyiImpl));
    assertSame(未 , Companion.getFun天鉞().invoke(庚 , tianyiImpl));
    assertSame(寅 , Companion.getFun天鉞().invoke(辛 , tianyiImpl));
    assertSame(巳 , Companion.getFun天鉞().invoke(壬 , tianyiImpl));
    assertSame(巳 , Companion.getFun天鉞().invoke(癸 , tianyiImpl));
  }

  @Test
  public void test祿存() {
    assertSame(寅 , Companion.getFun祿存().invoke(甲));
    assertSame(卯 , Companion.getFun祿存().invoke(乙));
    assertSame(巳 , Companion.getFun祿存().invoke(丙));
    assertSame(午 , Companion.getFun祿存().invoke(丁));
    assertSame(巳 , Companion.getFun祿存().invoke(戊));
    assertSame(午 , Companion.getFun祿存().invoke(己));
    assertSame(申 , Companion.getFun祿存().invoke(庚));
    assertSame(酉 , Companion.getFun祿存().invoke(辛));
    assertSame(亥 , Companion.getFun祿存().invoke(壬));
    assertSame(子 , Companion.getFun祿存().invoke(癸));
  }

  @Test
  public void test天馬() {
    assertSame(寅 , Companion.getFun年馬_年支().invoke(子));
    assertSame(亥 , Companion.getFun年馬_年支().invoke(丑));
    assertSame(申 , Companion.getFun年馬_年支().invoke(寅));
    assertSame(巳 , Companion.getFun年馬_年支().invoke(卯));
    assertSame(寅 , Companion.getFun年馬_年支().invoke(辰));
    assertSame(亥 , Companion.getFun年馬_年支().invoke(巳));
    assertSame(申 , Companion.getFun年馬_年支().invoke(午));
    assertSame(巳 , Companion.getFun年馬_年支().invoke(未));
    assertSame(寅 , Companion.getFun年馬_年支().invoke(申));
    assertSame(亥 , Companion.getFun年馬_年支().invoke(酉));
    assertSame(申 , Companion.getFun年馬_年支().invoke(戌));
    assertSame(巳 , Companion.getFun年馬_年支().invoke(亥));

    assertSame(寅 , Companion.getFun月馬_月數().invoke(11));
    assertSame(亥 , Companion.getFun月馬_月數().invoke(12));
    assertSame(申 , Companion.getFun月馬_月數().invoke(1));
    assertSame(巳 , Companion.getFun月馬_月數().invoke(2));
    assertSame(寅 , Companion.getFun月馬_月數().invoke(3));
    assertSame(亥 , Companion.getFun月馬_月數().invoke(4));
    assertSame(申 , Companion.getFun月馬_月數().invoke(5));
    assertSame(巳 , Companion.getFun月馬_月數().invoke(6));
    assertSame(寅 , Companion.getFun月馬_月數().invoke(7));
    assertSame(亥 , Companion.getFun月馬_月數().invoke(8));
    assertSame(申 , Companion.getFun月馬_月數().invoke(9));
    assertSame(巳 , Companion.getFun月馬_月數().invoke(10));
  }
}