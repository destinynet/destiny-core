/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.LuckyStar.右弼;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.TransFour.Type.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("Duplicates")
public class TransFourSouthImplTest {

  TransFour impl = new TransFourSouthImpl();

  @Test
  public void getStarOf() {
    assertSame(右弼, impl.getStarOf(戊, 科));
    assertSame(天機, impl.getStarOf(戊, 忌));

    assertSame(天同, impl.getStarOf(庚, 科));
    assertSame(太陰, impl.getStarOf(庚, 忌));

    assertSame(天府, impl.getStarOf(壬, 科));
    assertSame(武曲, impl.getStarOf(壬, 忌));
  }

  @Test
  public void getTypeOf() {
    assertNull(impl.getTypeOf(天機, 甲).orElse(null));
    assertSame(祿, impl.getTypeOf(天機, 乙).orElse(null));
    assertSame(權, impl.getTypeOf(天機, 丙).orElse(null));
    assertSame(科, impl.getTypeOf(天機, 丁).orElse(null));
    assertSame(忌, impl.getTypeOf(天機, 戊).orElse(null));
    assertNull(impl.getTypeOf(天機, 己).orElse(null));
    assertNull(impl.getTypeOf(天機, 庚).orElse(null));
    assertNull(impl.getTypeOf(天機, 辛).orElse(null));
    assertNull(impl.getTypeOf(天機, 壬).orElse(null));
    assertNull(impl.getTypeOf(天機, 癸).orElse(null));
  }
}