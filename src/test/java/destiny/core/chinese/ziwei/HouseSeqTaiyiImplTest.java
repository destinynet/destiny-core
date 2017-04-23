/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.ziwei.House.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class HouseSeqTaiyiImplTest {

  private IHouseSeq impl = new HouseSeqTaiyiImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testString() {
    logger.info("title tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.SIMPLIFIED_CHINESE));
  }

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

  @Test
  public void getAheadOf() {
    assertEquals(0 , impl.getAheadOf(官祿 , 官祿));
    assertEquals(1 , impl.getAheadOf(交友 , 官祿));
    assertEquals(2 , impl.getAheadOf(疾厄 , 官祿));
    assertEquals(3 , impl.getAheadOf(福德 , 官祿));
    assertEquals(4 , impl.getAheadOf(相貌 , 官祿));
    assertEquals(5 , impl.getAheadOf(父母 , 官祿));
    assertEquals(6 , impl.getAheadOf(命宮 , 官祿));
    assertEquals(7 , impl.getAheadOf(兄弟 , 官祿));
    assertEquals(8 , impl.getAheadOf(夫妻 , 官祿));
    assertEquals(9 , impl.getAheadOf(子女 , 官祿));
    assertEquals(10 , impl.getAheadOf(財帛 , 官祿));
    assertEquals(11 , impl.getAheadOf(田宅 , 官祿));
    assertEquals(0 , impl.getAheadOf(官祿 , 官祿));

    assertEquals(-1 , impl.getAheadOf(遷移 , 官祿)); // 太乙派 ，不存在 遷移宮
    assertEquals(-1 , impl.getAheadOf(官祿 , 遷移)); // 太乙派 ，不存在 遷移宮
    assertEquals(-1 , impl.getAheadOf(遷移 , 命宮)); // 太乙派 ，不存在 遷移宮
    assertEquals(-1 , impl.getAheadOf(命宮 , 遷移)); // 太乙派 ，不存在 遷移宮
  }

}