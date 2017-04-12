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
import static destiny.core.chinese.ziwei.LuckyStar.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class LuckyStarTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for(LuckyStar star : LuckyStar.values) {
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
    assertSame(辰 , fun左輔.apply(寅));
    assertSame(巳 , fun左輔.apply(卯));
    assertSame(午 , fun左輔.apply(辰));
    assertSame(未 , fun左輔.apply(巳));
    assertSame(申 , fun左輔.apply(午));
    assertSame(酉 , fun左輔.apply(未));
    assertSame(戌 , fun左輔.apply(申));
    assertSame(亥 , fun左輔.apply(酉));
    assertSame(子 , fun左輔.apply(戌));
    assertSame(丑 , fun左輔.apply(亥));
    assertSame(寅 , fun左輔.apply(子));
    assertSame(卯 , fun左輔.apply(丑));
  }

  @Test
  public void test右弼() {
    assertSame(戌 , fun右弼.apply(寅));
    assertSame(酉 , fun右弼.apply(卯));
    assertSame(申 , fun右弼.apply(辰));
    assertSame(未 , fun右弼.apply(巳));
    assertSame(午 , fun右弼.apply(午));
    assertSame(巳 , fun右弼.apply(未));
    assertSame(辰 , fun右弼.apply(申));
    assertSame(卯 , fun右弼.apply(酉));
    assertSame(寅 , fun右弼.apply(戌));
    assertSame(丑 , fun右弼.apply(亥));
    assertSame(子 , fun右弼.apply(子));
    assertSame(亥 , fun右弼.apply(丑));
  }

  @Test
  public void test天魁() {
    assertSame(丑 , fun天魁.apply(甲));
    assertSame(子 , fun天魁.apply(乙));
    assertSame(亥 , fun天魁.apply(丙));
    assertSame(亥 , fun天魁.apply(丁));
    assertSame(丑 , fun天魁.apply(戊));
    assertSame(子 , fun天魁.apply(己));
    assertSame(丑 , fun天魁.apply(庚));
    assertSame(午 , fun天魁.apply(辛));
    assertSame(卯 , fun天魁.apply(壬));
    assertSame(卯 , fun天魁.apply(癸));
  }

  @Test
  public void test天鉞() {
    assertSame(未 , fun天鉞.apply(甲));
    assertSame(申 , fun天鉞.apply(乙));
    assertSame(酉 , fun天鉞.apply(丙));
    assertSame(酉 , fun天鉞.apply(丁));
    assertSame(未 , fun天鉞.apply(戊));
    assertSame(申 , fun天鉞.apply(己));
    assertSame(未 , fun天鉞.apply(庚));
    assertSame(寅 , fun天鉞.apply(辛));
    assertSame(巳 , fun天鉞.apply(壬));
    assertSame(巳 , fun天鉞.apply(癸));
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
    assertSame(寅 , fun天馬.apply(子));
    assertSame(亥 , fun天馬.apply(丑));
    assertSame(申 , fun天馬.apply(寅));
    assertSame(巳 , fun天馬.apply(卯));
    assertSame(寅 , fun天馬.apply(辰));
    assertSame(亥 , fun天馬.apply(巳));
    assertSame(申 , fun天馬.apply(午));
    assertSame(巳 , fun天馬.apply(未));
    assertSame(寅 , fun天馬.apply(申));
    assertSame(亥 , fun天馬.apply(酉));
    assertSame(申 , fun天馬.apply(戌));
    assertSame(巳 , fun天馬.apply(亥));
  }
}