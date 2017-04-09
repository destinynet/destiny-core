/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.ziwei.House.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class HouseSeqDefaultImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

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

  @Test
  public void getAheadOf() {
    assertEquals(0 , impl.getAheadOf(命宮 , 命宮));
    assertEquals(1 , impl.getAheadOf(兄弟 , 命宮));
    assertEquals(2 , impl.getAheadOf(夫妻 , 命宮));
    assertEquals(3 , impl.getAheadOf(子女 , 命宮));
    assertEquals(4 , impl.getAheadOf(財帛 , 命宮));
    assertEquals(5 , impl.getAheadOf(疾厄 , 命宮));
    assertEquals(6 , impl.getAheadOf(遷移 , 命宮));
    assertEquals(7 , impl.getAheadOf(交友 , 命宮));
    assertEquals(8 , impl.getAheadOf(官祿 , 命宮));
    assertEquals(9 , impl.getAheadOf(田宅 , 命宮));
    assertEquals(10 , impl.getAheadOf(福德 , 命宮));
    assertEquals(11 , impl.getAheadOf(父母 , 命宮));

    assertEquals(-1 , impl.getAheadOf(相貌 , 命宮)); // 全書派，不存在 相貌宮
    assertEquals(-1 , impl.getAheadOf(命宮 , 相貌)); // 全書派，不存在 相貌宮
    assertEquals(-1 , impl.getAheadOf(相貌 , 兄弟)); // 全書派，不存在 相貌宮
    assertEquals(-1 , impl.getAheadOf(兄弟 , 相貌)); // 全書派，不存在 相貌宮
  }


}