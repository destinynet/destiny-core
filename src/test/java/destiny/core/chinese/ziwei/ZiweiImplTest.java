/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.ziwei.House.*;
import static org.junit.Assert.assertSame;

public class ZiweiImplTest {

  private HouseSeqIF seq = new HouseSeqDefaultImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 測試命宮
   * 已知：
   * 農曆三月、戌時 , 命宮在午
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  public void testFirstHouse() {
    ZiweiIF impl = new ZiweiImpl();
    assertSame(Branch.午 , impl.getFirstHouse(3 , Branch.戌));
  }

  /**
   * 命宮決定後，逆時針飛佈 12宮
   *
   * 已知：
   * 農曆三月、戌時 , 命宮在午
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  public void testHouse() {
    ZiweiIF impl = new ZiweiImpl();

    assertSame(Branch.午 , impl.getHouse(3 , Branch.戌 , 命宮 , seq));
    assertSame(Branch.巳 , impl.getHouse(3 , Branch.戌 , 兄弟 , seq));
    assertSame(Branch.辰 , impl.getHouse(3 , Branch.戌 , 夫妻 , seq));

    assertSame(Branch.卯 , impl.getHouse(3 , Branch.戌 , 子女 , seq));
    assertSame(Branch.寅 , impl.getHouse(3 , Branch.戌 , 財帛 , seq));
    assertSame(Branch.丑 , impl.getHouse(3 , Branch.戌 , 疾厄 , seq));

    assertSame(Branch.子 , impl.getHouse(3 , Branch.戌 , 遷移 , seq));
    assertSame(Branch.亥 , impl.getHouse(3 , Branch.戌 , 交友 , seq));
    assertSame(Branch.戌 , impl.getHouse(3 , Branch.戌 , 官祿 , seq));

    assertSame(Branch.酉 , impl.getHouse(3 , Branch.戌 , 田宅 , seq));
    assertSame(Branch.申 , impl.getHouse(3 , Branch.戌 , 福德 , seq));
    assertSame(Branch.未 , impl.getHouse(3 , Branch.戌 , 父母 , seq));
  }
}