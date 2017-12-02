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
import static destiny.core.chinese.ziwei.StarMain.Companion;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class TransFourFullBookImplTest {

  ITransFour impl = new TransFourFullBookImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testTitle() {
    logger.info("title tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

  @Test
  public void getStarOf() {
    assertSame(StarLucky.Companion.get右弼(), impl.getStarOf(戊, 科));
    assertSame(Companion.get天機(), impl.getStarOf(戊, 忌));

    assertSame(Companion.get天同(), impl.getStarOf(庚, 科));
    assertSame(Companion.get太陰(), impl.getStarOf(庚, 忌));

    assertSame(Companion.get天府(), impl.getStarOf(壬, 科));
    assertSame(Companion.get武曲(), impl.getStarOf(壬, 忌));
  }

  @Test
  public void getValueOf() {
    assertNull(impl.getValueOf(Companion.get天機(), 甲).orElse(null));
    assertSame(祿, impl.getValueOf(Companion.get天機(), 乙).orElse(null));
    assertSame(權, impl.getValueOf(Companion.get天機(), 丙).orElse(null));
    assertSame(科, impl.getValueOf(Companion.get天機(), 丁).orElse(null));
    assertSame(忌, impl.getValueOf(Companion.get天機(), 戊).orElse(null));
    assertNull(impl.getValueOf(Companion.get天機(), 己).orElse(null));
    assertNull(impl.getValueOf(Companion.get天機(), 庚).orElse(null));
    assertNull(impl.getValueOf(Companion.get天機(), 辛).orElse(null));
    assertNull(impl.getValueOf(Companion.get天機(), 壬).orElse(null));
    assertNull(impl.getValueOf(Companion.get天機(), 癸).orElse(null));
  }
}