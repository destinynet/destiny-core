/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple3;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.House.*;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.ZiweiIF.getBranchOfPurpleStar;
import static org.junit.Assert.assertEquals;
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
    assertSame(午 , ZiweiIF.getMainHouseBranch(3 , 戌));

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
    assertSame(Branch.寅 , ZiweiIF.getBodyHouse(3 , 戌));

    // 丁酉年 3月14日，子時，身命同宮 , 都在 辰
    assertSame(辰 , ZiweiIF.getMainHouseBranch(3 , Branch.子));
    assertSame(辰 , ZiweiIF.getBodyHouse(3 , Branch.子));
  }

  /**
   * 納音 五行局
   *
   * 已知：
   * 丁酉年 (2017)
   * 農曆三月、戌時 , 命宮在寅 , 水二局[天河水]
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  public void testGetNaYin() {
    ZiweiIF impl = new ZiweiImpl();
    Tuple3<String , FiveElement , Integer> t3 = impl.getNaYin(丁 , 3 , 戌);

    assertEquals("天河水" , t3.v1());
    assertSame(FiveElement.水 , t3.v2());
    assertSame(2 , t3.v3());
  }

  /**
   * 計算紫微星所在宮位 , 驗證資料見此教學頁面 http://bit.ly/2oo2hZz
   */
  @Test
  public void testGetPurpleStar() {
    ZiweiIF impl = new ZiweiImpl();

    // 假設某個人出生日是23日，五行局為金四局 ==> 紫微在午
    assertSame(午 , getBranchOfPurpleStar(4 , 23));

    // 假設有一個人是24日生，金四局 ==> 紫微在未
    assertSame(未 , getBranchOfPurpleStar(4 , 24));

    // ex：有個人出生年月日是西元 1980年(民69庚申) 農曆 7月23日 丑時 (男) ==> 紫微在 甲申 , 天府也在甲申 (紫微、天府 同宮)
    assertSame(StemBranch.get(甲 , 申) , impl.getStemBranchBranchOfPurpleStar(庚 , 3 , 23));
    assertSame(StemBranch.get(甲 , 申) , impl.getStemBranchOfTianFuStar(庚 , 3 , 23));

    // 已知 : 2017(丁酉年)-04-10 (農曆 三月14日） 未時 , 土5局 , 紫微 在 癸卯 , 天府 在 癸丑
    assertSame(StemBranch.get(癸 , 卯) , impl.getStemBranchBranchOfPurpleStar(丁 , 5 , 14));
    assertSame(StemBranch.get(癸 , 丑) , impl.getStemBranchOfTianFuStar(丁 , 5 , 14));


    // 已知 : 2017(丁酉年)-04-10 (農曆 三月14日） 酉時 , 水2局 , 紫微、天府 都在戊申
    assertSame(StemBranch.get(戊 , 申) , impl.getStemBranchBranchOfPurpleStar(丁 , 2 , 14));
    assertSame(StemBranch.get(戊 , 申) , impl.getStemBranchOfTianFuStar(丁 , 2 , 14));
  }

  @Test
  public void testHouseOf() {
    ZiweiIF impl = new ZiweiImpl();

    // 範例 : http://sweeteason.pixnet.net/blog/post/43447102
    // ex：有個人出生年月日是西元 1980年(民69庚申) 農曆 7月23日 丑時 (男) , 木三局 ==> 紫微在 甲申 , 天府也在甲申 (紫微、天府 同宮)
    assertSame(StemBranch.get(甲, 申), impl.getStemBranchOf(紫微, 庚, 3, 23));
    assertSame(StemBranch.get(癸, 未), impl.getStemBranchOf(天機, 庚, 3, 23));
    assertSame(StemBranch.get(辛, 巳), impl.getStemBranchOf(太陽, 庚, 3, 23));
    assertSame(StemBranch.get(庚, 辰), impl.getStemBranchOf(武曲, 庚, 3, 23));
    assertSame(StemBranch.get(己, 卯), impl.getStemBranchOf(天同, 庚, 3, 23));
    assertSame(StemBranch.get(戊, 子), impl.getStemBranchOf(廉貞, 庚, 3, 23));

    assertSame(StemBranch.get(甲, 申), impl.getStemBranchOf(天府, 庚, 3, 23));
    assertSame(StemBranch.get(乙, 酉), impl.getStemBranchOf(太陰, 庚, 3, 23));
    assertSame(StemBranch.get(丙, 戌), impl.getStemBranchOf(貪狼, 庚, 3, 23));
    assertSame(StemBranch.get(丁, 亥), impl.getStemBranchOf(巨門, 庚, 3, 23));
    assertSame(StemBranch.get(戊, 子), impl.getStemBranchOf(天相, 庚, 3, 23));
    assertSame(StemBranch.get(己, 丑), impl.getStemBranchOf(天梁, 庚, 3, 23));
    assertSame(StemBranch.get(戊, 寅), impl.getStemBranchOf(七殺, 庚, 3, 23));
    assertSame(StemBranch.get(壬, 午), impl.getStemBranchOf(破軍, 庚, 3, 23));
  }
}