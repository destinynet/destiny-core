/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.*;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class TransFourFullCollectImplTest {

  private ITransFour impl = new TransFourFullCollectImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testTitle() {
    logger.info("title tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

  @Test
  public void getStarOf() {

    assertSame(StarLucky.Companion.get右弼(), impl.getStarOf(戊, 科));
    assertSame(StarMain.天機.INSTANCE , impl.getStarOf(戊, 忌));

    assertSame(StarMain.太陰.INSTANCE, impl.getStarOf(庚, 科));
    assertSame(StarMain.天同.INSTANCE, impl.getStarOf(庚, 忌));

    assertSame(StarLucky.Companion.get左輔(), impl.getStarOf(壬, 科));
    assertSame(StarMain.武曲.INSTANCE, impl.getStarOf(壬, 忌));
  }

  @Test
  public void getValueOf() {
    assertSame(祿 , impl.getValueOf(StarMain.廉貞.INSTANCE, 甲).orElse(null));
    assertSame(null , impl.getValueOf(StarMain.廉貞.INSTANCE, 乙).orElse(null));
    assertSame(忌, impl.getValueOf(StarMain.廉貞.INSTANCE, 丙).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 丁).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 戊).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 己).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 庚).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 辛).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 壬).orElse(null));
    assertSame(null, impl.getValueOf(StarMain.廉貞.INSTANCE , 癸).orElse(null));
  }
}