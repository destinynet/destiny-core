/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.LuckyStar.右弼;
import static destiny.core.chinese.ziwei.LuckyStar.左輔;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class TransFourNorthImplTest {

  ITransFour impl = new TransFourNorthImpl();

  @Test
  public void getStarOf() {
    assertSame(右弼, impl.getStarOf(戊, 科));
    assertSame(天機, impl.getStarOf(戊, 忌));

    assertSame(天同, impl.getStarOf(庚, 科));
    assertSame(天相, impl.getStarOf(庚, 忌));

    assertSame(左輔, impl.getStarOf(壬, 科));
    assertSame(武曲, impl.getStarOf(壬, 忌));
  }

  @Test
  public void getValueOf() {
    assertSame(科 , impl.getValueOf(武曲 , 甲).orElse(null));
    assertNull(impl.getValueOf(武曲 , 乙).orElse(null));
    assertNull(impl.getValueOf(武曲 , 丙).orElse(null));
    assertNull(impl.getValueOf(武曲 , 丁).orElse(null));
    assertNull(impl.getValueOf(武曲 , 戊).orElse(null));
    assertSame(祿, impl.getValueOf(武曲 , 己).orElse(null));
    assertSame(權, impl.getValueOf(武曲 , 庚).orElse(null));
    assertNull(impl.getValueOf(武曲 , 辛).orElse(null));
    assertSame(忌, impl.getValueOf(武曲 , 壬).orElse(null));
    assertNull(impl.getValueOf(武曲 , 癸).orElse(null));
  }
}