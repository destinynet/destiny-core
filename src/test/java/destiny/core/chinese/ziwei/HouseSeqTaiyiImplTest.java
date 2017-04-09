/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.ziwei.House.*;
import static org.junit.Assert.assertSame;

public class HouseSeqTaiyiImplTest {

  private HouseSeqIF impl = new HouseSeqTaiyiImpl();

  @Test
  public void next() {
    assertSame(官祿, 官祿.next(-24, impl));
    assertSame(官祿, 官祿.next(-12, impl));
    assertSame(交友, 官祿.next(-11, impl));
    assertSame(財帛, 官祿.next(-2, impl));
    assertSame(田宅, 官祿.next(-1, impl));
    assertSame(官祿, 官祿.next(0, impl));
    assertSame(交友, 官祿.next(1, impl));
    assertSame(田宅, 官祿.next(11, impl));
    assertSame(官祿, 官祿.next(12, impl));
    assertSame(官祿, 官祿.next(24, impl));
  }

  @Test
  public void prev() {
    assertSame(官祿, 官祿.prev(-24, impl));
    assertSame(官祿, 官祿.prev(-12, impl));
    assertSame(田宅, 官祿.prev(-11, impl));
    assertSame(疾厄, 官祿.prev(-2, impl));
    assertSame(交友, 官祿.prev(-1, impl));
    assertSame(官祿, 官祿.prev(0, impl));
    assertSame(田宅, 官祿.prev(1, impl));
    assertSame(財帛, 官祿.prev(2, impl));
    assertSame(交友, 官祿.prev(11, impl));
    assertSame(官祿, 官祿.prev(12, impl));
    assertSame(官祿, 官祿.prev(24, impl));


  }

}