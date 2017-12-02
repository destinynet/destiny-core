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

public class TransFourZiyunImplTest {

  ITransFour impl = new TransFourZiyunImpl();
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testTitle() {
    logger.info("title tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

  @Test
  public void getStarOf() {
    assertSame(Companion.get太陽(), impl.getStarOf(癸, 科));
  }

  @Test
  public void getValueOf() {
    assertSame(忌 , impl.getValueOf(Companion.get太陽(), 甲).orElse(null));
    assertNull(impl.getValueOf(Companion.get太陽(), 乙).orElse(null));
    assertNull(impl.getValueOf(Companion.get太陽(), 丙).orElse(null));
    assertNull(impl.getValueOf(Companion.get太陽(), 丁).orElse(null));
    assertNull(impl.getValueOf(Companion.get太陽(), 戊).orElse(null));
    assertNull(impl.getValueOf(Companion.get太陽(), 己).orElse(null));
    assertSame(祿 , impl.getValueOf(Companion.get太陽(), 庚).orElse(null));
    assertSame(權 , impl.getValueOf(Companion.get太陽(), 辛).orElse(null));
    assertNull(impl.getValueOf(Companion.get太陽(), 壬).orElse(null));
    assertSame(科 , impl.getValueOf(Companion.get太陽(), 癸).orElse(null));
  }
}