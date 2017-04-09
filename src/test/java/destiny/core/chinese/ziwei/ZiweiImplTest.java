/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.House.*;
import static org.junit.Assert.assertSame;

public class ZiweiImplTest {

  private HouseSeqIF seq = new HouseSeqDefaultImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 測試命宮 (main)
   * 已知：
   * 農曆三月、戌時 , 命宮在午 ,
   *
   * 106丁酉年 , 命宮 為「丙午」
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  public void testGetMainHouseBranch() {
    ZiweiIF impl = new ZiweiImpl();
    assertSame(午 , impl.getMainHouseBranch(3 , 戌));

    assertSame(StemBranch.get(丙 , 午) , impl.getMainHouse(丁 , 3 , 戌));
  }

  /**
   * 命宮決定後，逆時針飛佈 12宮
   *
   * 已知：
   * 農曆三月、戌時 , 命宮在午
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  public void testGetHouseBranch() {
    ZiweiIF impl = new ZiweiImpl();

    assertSame(午 , impl.getHouseBranch(3 , 戌 , 命宮 , seq));
    assertSame(巳 , impl.getHouseBranch(3 , 戌 , 兄弟 , seq));
    assertSame(辰 , impl.getHouseBranch(3 , 戌 , 夫妻 , seq));
    assertSame(卯 , impl.getHouseBranch(3 , 戌 , 子女 , seq));
    assertSame(寅 , impl.getHouseBranch(3 , 戌 , 財帛 , seq));
    assertSame(丑 , impl.getHouseBranch(3 , 戌 , 疾厄 , seq));
    assertSame(子 , impl.getHouseBranch(3 , 戌 , 遷移 , seq));
    assertSame(亥 , impl.getHouseBranch(3 , 戌 , 交友 , seq));
    assertSame(戌 , impl.getHouseBranch(3 , 戌 , 官祿 , seq));
    assertSame(酉 , impl.getHouseBranch(3 , 戌 , 田宅 , seq));
    assertSame(申 , impl.getHouseBranch(3 , 戌 , 福德 , seq));
    assertSame(未 , impl.getHouseBranch(3 , 戌 , 父母 , seq));
  }

  /**
   * 承上 ，取得宮位天干
   *
   * 已知：
   * 丁酉年
   * 農曆三月、戌時 , 命宮在午
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  public void testHouseWithStem() {
    ZiweiIF impl = new ZiweiImpl();

    assertSame(StemBranch.get(丙 , 午) , impl.getHouse(丁 , 3 , 戌 , 命宮 , seq));
    assertSame(StemBranch.get(乙 , 巳) , impl.getHouse(丁 , 3 , 戌 , 兄弟 , seq));
    assertSame(StemBranch.get(甲 , 辰) , impl.getHouse(丁 , 3 , 戌 , 夫妻 , seq));
    assertSame(StemBranch.get(癸 , 卯) , impl.getHouse(丁 , 3 , 戌 , 子女 , seq));
    assertSame(StemBranch.get(壬 , 寅) , impl.getHouse(丁 , 3 , 戌 , 財帛 , seq));
    assertSame(StemBranch.get(癸 , 丑) , impl.getHouse(丁 , 3 , 戌 , 疾厄 , seq));
    assertSame(StemBranch.get(壬 , 子) , impl.getHouse(丁 , 3 , 戌 , 遷移 , seq));
    assertSame(StemBranch.get(辛 , 亥) , impl.getHouse(丁 , 3 , 戌 , 交友 , seq));
    assertSame(StemBranch.get(庚 , 戌) , impl.getHouse(丁 , 3 , 戌 , 官祿 , seq));
    assertSame(StemBranch.get(己 , 酉) , impl.getHouse(丁 , 3 , 戌 , 田宅 , seq));
    assertSame(StemBranch.get(戊 , 申) , impl.getHouse(丁 , 3 , 戌 , 福德 , seq));
    assertSame(StemBranch.get(丁 , 未) , impl.getHouse(丁 , 3 , 戌 , 父母 , seq));
  }

  /**
   * 身宮
   *
   * 已知：
   * 農曆三月、戌時 , 命宮在寅
   * 比對資料 : https://goo.gl/w4snjL
   * */
  @Test
  public void testBodyHouse() {
    ZiweiIF impl = new ZiweiImpl();
    assertSame(Branch.寅 , impl.getBodyHouse(3 , 戌));

    // 丁酉年 3月14日，子時，身命同宮 , 都在 辰
    assertSame(辰 , impl.getMainHouseBranch(3 , Branch.子));
    assertSame(辰 , impl.getBodyHouse(3 , Branch.子));

  }
}