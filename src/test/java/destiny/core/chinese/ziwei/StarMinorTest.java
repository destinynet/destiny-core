/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.丁;
import static destiny.core.chinese.StemBranch.己酉;
import static destiny.core.chinese.ziwei.StarMinor.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class StarMinorTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testToString() {
    for (StarMinor star : StarMinor.values) {
      assertNotNull(star.toString());
      assertNotNull(star.toString(Locale.TAIWAN));
      assertNotNull(star.toString(Locale.CHINA));
      logger.info("tw = {}({}) , cn = {}({})" ,
        star.toString(Locale.TAIWAN) , star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA) , star.getAbbreviation(Locale.CHINA));
    }
  }

  @Test
  public void test天刑() {
    assertSame(酉 , fun天刑.apply(寅));
    assertSame(戌 , fun天刑.apply(卯));
    assertSame(亥 , fun天刑.apply(辰));
    assertSame(子 , fun天刑.apply(巳));
    assertSame(丑 , fun天刑.apply(午));
    assertSame(寅 , fun天刑.apply(未));
    assertSame(卯 , fun天刑.apply(申));
    assertSame(辰 , fun天刑.apply(酉));
    assertSame(巳 , fun天刑.apply(戌));
    assertSame(午 , fun天刑.apply(亥));
    assertSame(未 , fun天刑.apply(子));
    assertSame(申 , fun天刑.apply(丑));
  }

  @Test
  public void test天姚() {
    assertSame(丑 , fun天姚.apply(寅));
    assertSame(寅 , fun天姚.apply(卯));
    assertSame(卯 , fun天姚.apply(辰));
    assertSame(辰 , fun天姚.apply(巳));
    assertSame(巳 , fun天姚.apply(午));
    assertSame(午 , fun天姚.apply(未));
    assertSame(未 , fun天姚.apply(申));
    assertSame(申 , fun天姚.apply(酉));
    assertSame(酉 , fun天姚.apply(戌));
    assertSame(戌 , fun天姚.apply(亥));
    assertSame(亥 , fun天姚.apply(子));
    assertSame(子 , fun天姚.apply(丑));
  }

  @Test
  public void test天月() {
    assertSame(戌 , fun天月.apply(寅));
    assertSame(巳 , fun天月.apply(卯));
    assertSame(辰 , fun天月.apply(辰));
    assertSame(寅 , fun天月.apply(巳));
    assertSame(未 , fun天月.apply(午));
    assertSame(卯 , fun天月.apply(未));
    assertSame(亥 , fun天月.apply(申));
    assertSame(未 , fun天月.apply(酉));
    assertSame(寅 , fun天月.apply(戌));
    assertSame(午 , fun天月.apply(亥));
    assertSame(戌 , fun天月.apply(子));
    assertSame(午 , fun天月.apply(丑));
  }

  @Test
  public void test天哭() {
    assertSame(午 , fun天哭.apply(子));
    assertSame(巳 , fun天哭.apply(丑));
    assertSame(辰 , fun天哭.apply(寅));
    assertSame(卯 , fun天哭.apply(卯));
    assertSame(寅 , fun天哭.apply(辰));
    assertSame(丑 , fun天哭.apply(巳));
    assertSame(子 , fun天哭.apply(午));
    assertSame(亥 , fun天哭.apply(未));
    assertSame(戌 , fun天哭.apply(申));
    assertSame(酉 , fun天哭.apply(酉));
    assertSame(申 , fun天哭.apply(戌));
    assertSame(未 , fun天哭.apply(亥));
  }

  @Test
  public void test鳳閣() {
    assertSame(戌 , fun鳳閣.apply(子));
    assertSame(酉 , fun鳳閣.apply(丑));
    assertSame(申 , fun鳳閣.apply(寅));
    assertSame(未 , fun鳳閣.apply(卯));
    assertSame(午 , fun鳳閣.apply(辰));
    assertSame(巳 , fun鳳閣.apply(巳));
    assertSame(辰 , fun鳳閣.apply(午));
    assertSame(卯 , fun鳳閣.apply(未));
    assertSame(寅 , fun鳳閣.apply(申));
    assertSame(丑 , fun鳳閣.apply(酉));
    assertSame(子 , fun鳳閣.apply(戌));
    assertSame(亥 , fun鳳閣.apply(亥));
  }

  @Test
  public void test紅鸞() {
    assertSame(卯 , fun紅鸞.apply(子));
    assertSame(寅 , fun紅鸞.apply(丑));
    assertSame(丑 , fun紅鸞.apply(寅));
    assertSame(子 , fun紅鸞.apply(卯));
    assertSame(亥 , fun紅鸞.apply(辰));
    assertSame(戌 , fun紅鸞.apply(巳));
    assertSame(酉 , fun紅鸞.apply(午));
    assertSame(申 , fun紅鸞.apply(未));
    assertSame(未 , fun紅鸞.apply(申));
    assertSame(午 , fun紅鸞.apply(酉));
    assertSame(巳 , fun紅鸞.apply(戌));
    assertSame(辰 , fun紅鸞.apply(亥));
  }

  @Test
  public void test天喜() {
    assertSame(酉 , fun天喜.apply(子));
    assertSame(申 , fun天喜.apply(丑));
    assertSame(未 , fun天喜.apply(寅));
    assertSame(午 , fun天喜.apply(卯));
    assertSame(巳 , fun天喜.apply(辰));
    assertSame(辰 , fun天喜.apply(巳));
    assertSame(卯 , fun天喜.apply(午));
    assertSame(寅 , fun天喜.apply(未));
    assertSame(丑 , fun天喜.apply(申));
    assertSame(子 , fun天喜.apply(酉));
    assertSame(亥 , fun天喜.apply(戌));
    assertSame(戌 , fun天喜.apply(亥));
  }

  @Test
  public void test天德() {
    assertSame(酉 , fun天德.apply(子));
    assertSame(戌 , fun天德.apply(丑));
    assertSame(亥 , fun天德.apply(寅));
    assertSame(子 , fun天德.apply(卯));
    assertSame(丑 , fun天德.apply(辰));
    assertSame(寅 , fun天德.apply(巳));
    assertSame(卯 , fun天德.apply(午));
    assertSame(辰 , fun天德.apply(未));
    assertSame(巳 , fun天德.apply(申));
    assertSame(午 , fun天德.apply(酉));
    assertSame(未 , fun天德.apply(戌));
    assertSame(申 , fun天德.apply(亥));
  }

  @Test
  public void test月德() {
    assertSame(巳 , fun月德.apply(子));
    assertSame(午 , fun月德.apply(丑));
    assertSame(未 , fun月德.apply(寅));
    assertSame(申 , fun月德.apply(卯));
    assertSame(酉 , fun月德.apply(辰));
    assertSame(戌 , fun月德.apply(巳));
    assertSame(亥 , fun月德.apply(午));
    assertSame(子 , fun月德.apply(未));
    assertSame(丑 , fun月德.apply(申));
    assertSame(寅 , fun月德.apply(酉));
    assertSame(卯 , fun月德.apply(戌));
    assertSame(辰 , fun月德.apply(亥));
  }

  @Test
  public void test日系星() {
    // 2017-04-12 丁酉年 三月十六日 巳時,
    // 三台在 (己)酉
    assertSame(酉 , fun三台.apply(辰 , 16));
    // 八座在 (乙)巳
    assertSame(巳 , fun八座.apply(辰 , 16));
    // 恩光在 (丁)未
    assertSame(未 , fun恩光.apply(16 , 巳));
    // 天貴在 (辛)亥
    assertSame(亥 , fun天貴.apply(16 , 巳));
  }

  @Test
  public void test天才天壽() {
    // 2017-04-12 丁酉年 三月十六日 巳時

    // 命宮 在 (辛)亥
    assertSame(亥 , IZiwei.getMainHouseBranch(3 , 巳));
    // 天才 在 (戊)申
    assertSame(申 , fun天才.apply(酉 , 3 , 巳));

    // 身宮 在 (己)酉
    assertSame(酉 , IZiwei.getBodyHouseBranch(3 , 巳));
    assertSame(己酉 , IZiwei.getBodyHouse(丁 , 3 , 巳));
    // 天壽 在 (丙)午
    assertSame(午 , fun天壽.apply(酉 , 3 , 巳));

  }


}