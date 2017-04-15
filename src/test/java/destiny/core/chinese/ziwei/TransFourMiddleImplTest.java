/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.忌;
import static destiny.core.chinese.ziwei.ITransFour.Value.科;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class TransFourMiddleImplTest {

  ITransFour impl = new TransFourMiddleImpl();

  @Test
  public void getStarOf() {

    assertSame(太陽, impl.getStarOf(戊, 科));
    assertSame(天機, impl.getStarOf(戊, 忌));

    assertSame(天府, impl.getStarOf(庚, 科));
    assertSame(天同, impl.getStarOf(庚, 忌));

    assertSame(天府, impl.getStarOf(壬, 科));
    assertSame(武曲, impl.getStarOf(壬, 忌));
  }

  @Test
  public void getValueOf() {
    assertNull(impl.getValueOf(天府 , 甲).orElse(null));
    assertNull(impl.getValueOf(天府 , 乙).orElse(null));
    assertNull(impl.getValueOf(天府 , 丙).orElse(null));
    assertNull(impl.getValueOf(天府 , 丁).orElse(null));
    assertNull(impl.getValueOf(天府 , 戊).orElse(null));
    assertNull(impl.getValueOf(天府 , 己).orElse(null));
    assertSame(科 , impl.getValueOf(天府 , 庚).orElse(null));
    assertNull(impl.getValueOf(天府 , 辛).orElse(null));
    assertSame(科 , impl.getValueOf(天府 , 壬).orElse(null));
    assertNull(impl.getValueOf(天府 , 癸).orElse(null));
  }
}