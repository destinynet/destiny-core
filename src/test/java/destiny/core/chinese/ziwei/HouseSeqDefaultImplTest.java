/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.ziwei.House.*;
import static org.junit.Assert.assertSame;

public class HouseSeqDefaultImplTest {

  HouseSeqIF impl = new HouseSeqDefaultImpl();

  @Test
  public void next() {
    assertSame(命宮, 命宮.next(-36, impl));
    assertSame(命宮, 命宮.next(-24, impl));
    assertSame(命宮, 命宮.next(-12, impl));
    assertSame(兄弟, 命宮.next(-11, impl));
    assertSame(福德, 命宮.next(-2, impl));
    assertSame(父母, 命宮.next(-1, impl));
    assertSame(命宮, 命宮.next(0, impl));
    assertSame(兄弟, 命宮.next(1, impl));
    assertSame(父母, 命宮.next(11, impl));
    assertSame(命宮, 命宮.next(12, impl));
    assertSame(命宮, 命宮.next(24, impl));
  }

  @Test
  public void prev() {
    assertSame(命宮, 命宮.prev(-36, impl));
    assertSame(命宮, 命宮.prev(-24, impl));
    assertSame(命宮, 命宮.prev(-12, impl));
    assertSame(父母, 命宮.prev(-11, impl));
    assertSame(夫妻, 命宮.prev(-2, impl));
    assertSame(兄弟, 命宮.prev(-1, impl));
    assertSame(命宮, 命宮.prev(0, impl));
    assertSame(父母, 命宮.prev(1, impl));
    assertSame(福德, 命宮.prev(2, impl));
    assertSame(兄弟, 命宮.prev(11, impl));
    assertSame(命宮, 命宮.prev(12, impl));
    assertSame(命宮, 命宮.prev(24, impl));
  }

}