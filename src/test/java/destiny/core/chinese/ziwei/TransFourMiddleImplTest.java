/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.忌;
import static destiny.core.chinese.ziwei.ITransFour.Value.科;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class TransFourMiddleImplTest {

  ITransFour impl = new TransFourMiddleImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testTitle() {
    logger.info("title tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

  @Test
  public void getStarOf() {

    assertSame(Companion.get太陽(), impl.getStarOf(戊, 科));
    assertSame(Companion.get天機(), impl.getStarOf(戊, 忌));

    assertSame(Companion.get天府(), impl.getStarOf(庚, 科));
    assertSame(Companion.get天同(), impl.getStarOf(庚, 忌));

    assertSame(Companion.get天府(), impl.getStarOf(壬, 科));
    assertSame(Companion.get武曲(), impl.getStarOf(壬, 忌));
  }

  @Test
  public void getValueOf() {
    assertNull(impl.getValueOf(Companion.get天府(), 甲).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 乙).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 丙).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 丁).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 戊).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 己).orElse(null));
    assertSame(科 , impl.getValueOf(Companion.get天府(), 庚).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 辛).orElse(null));
    assertSame(科 , impl.getValueOf(Companion.get天府(), 壬).orElse(null));
    assertNull(impl.getValueOf(Companion.get天府(), 癸).orElse(null));
  }
}