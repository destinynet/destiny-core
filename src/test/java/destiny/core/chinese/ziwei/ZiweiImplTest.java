/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.Stem;
import destiny.core.chinese.ziwei.Settings.FireBell;
import destiny.core.chinese.ziwei.Settings.Horse;
import destiny.core.chinese.ziwei.Settings.HurtAngel;
import destiny.core.chinese.ziwei.Settings.Tianyi;
import org.jooq.lambda.tuple.Tuple3;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.StemBranch.*;
import static destiny.core.chinese.ziwei.House.*;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.ZiweiIF.getBranchOfPurpleStar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ZiweiImplTest {

  private HouseSeqIF seq = new HouseSeqDefaultImpl();

  ZiweiIF impl = new ZiweiImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 1970-01-01
   * 民58己酉年 , 十一月24日，子時 , 大驛土 , 水二局（澗下水）
   * 比對 十三行紫微斗數 排盤結果 : http://imgur.com/lcX9Q4o
   * (此圖用的 天使、天傷 是 {@link HurtAngel#FIXED} 版本)
   */
  @Test
  public void testPlate2() {
    Settings settings = new Settings(Tianyi.ZIWEI_BOOK, FireBell.全集, Horse.年馬, HurtAngel.FIXED, Settings.TransFour.DEFAULT);

    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(MainStar.values));
    starList.addAll(Arrays.asList(LuckyStar.values));
    starList.addAll(Arrays.asList(UnluckyStar.values));
    starList.addAll(Arrays.asList(MinorStar.values));
    starList.addAll(Arrays.asList(DoctorStar.values));

    Map<ITransFour.Type , Stem> transFourTypes = new ImmutableMap.Builder<ITransFour.Type , Stem>()
      .put(ITransFour.Type.大限 , 甲)
      .put(ITransFour.Type.流年 , 乙)
      .put(ITransFour.Type.流月 , 丙)
      .put(ITransFour.Type.流日 , 丁)
      .put(ITransFour.Type.流時 , 戊)
      .build();

    Plate plate = impl.getPlate(己酉 , 子 , 11 , 24 , 子 , seq , starList, Gender.男, transFourTypes , settings);

    logger.info("命宮 = {} , 身宮 = {} . {}{}局" , plate.getMainHouse() , plate.getBodyHouse() , plate.getFiveElement() , plate.getSet());
    logger.debug("宮位名稱 -> 宮位資料 = {}" , plate.getHouseMap());
    logger.debug("星體 -> 宮位地支 = {}" , plate.getStarMap());
    logger.debug("宮位名稱 -> 星體s = {}" , plate.getHouseStarMap());
    logger.debug("宮位地支 -> 星體s = {}" , plate.getBranchStarMap());
    logger.debug("houseDataSet = {}" , plate.getHouseDataSet());

    plate.getHouseDataSet().forEach(houseData -> {
      logger.info("{} ({}) : {}" , houseData.getStemBranch() , houseData.getHouse() , houseData.getStars());
    });

    plate.getTranFours().forEach((star, tuple2s) -> {
      logger.info("{} : {}" , star , tuple2s);
    });

    plate.getTransFourOf(天機).forEach(t -> {
      logger.info("{} : {} -> {}" , 天機 , t.v1() , t.v2());
    });
  }

  /**
   * 2017-04-14 亥時
   * 民106丁酉年 , 三月18日，火六局，覆燈火
   * 比對子由排盤結果 : http://imgur.com/EsUnXIK
   */
  @Test
  public void testPlate1() {
    Settings settings = new Settings(Tianyi.ZIWEI_BOOK, FireBell.全集, Horse.年馬, HurtAngel.YINYANG, Settings.TransFour.DEFAULT);

    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(MainStar.values));
    starList.addAll(Arrays.asList(LuckyStar.values));
    starList.addAll(Arrays.asList(UnluckyStar.values));
    starList.addAll(Arrays.asList(MinorStar.values));
    starList.addAll(Arrays.asList(DoctorStar.values));

    Plate plate = impl.getPlate(丁酉 , 辰 , 3 , 18 , 亥 , seq , starList, Gender.男, new HashMap<>(), settings);

    logger.debug("命宮 = {} , 身宮 = {} . {}{}局" , plate.getMainHouse() , plate.getBodyHouse() , plate.getFiveElement() , plate.getSet());
    logger.debug("宮位名稱 -> 宮位資料 = {}" , plate.getHouseMap());
    logger.debug("星體 -> 宮位地支 = {}" , plate.getStarMap());
    logger.debug("宮位名稱 -> 星體s = {}" , plate.getHouseStarMap());
    logger.debug("宮位地支 -> 星體s = {}" , plate.getBranchStarMap());
    logger.debug("houseDataSet = {}" , plate.getHouseDataSet());

    plate.getHouseDataSet().forEach(houseData -> {
      logger.info("{} ({}) : {}" , houseData.getStemBranch() , houseData.getHouse() , houseData.getStars());
    });
  }



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

    assertSame(午 , ZiweiIF.getMainHouseBranch(3 , 戌));

    assertSame(丙午 , ZiweiIF.getMainHouse(丁 , 3 , 戌));
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
    assertSame(午 , ZiweiIF.getHouseBranch(3 , 戌 , 命宮 , seq));
    assertSame(巳 , ZiweiIF.getHouseBranch(3 , 戌 , 兄弟 , seq));
    assertSame(辰 , ZiweiIF.getHouseBranch(3 , 戌 , 夫妻 , seq));
    assertSame(卯 , ZiweiIF.getHouseBranch(3 , 戌 , 子女 , seq));
    assertSame(寅 , ZiweiIF.getHouseBranch(3 , 戌 , 財帛 , seq));
    assertSame(丑 , ZiweiIF.getHouseBranch(3 , 戌 , 疾厄 , seq));
    assertSame(子 , ZiweiIF.getHouseBranch(3 , 戌 , 遷移 , seq));
    assertSame(亥 , ZiweiIF.getHouseBranch(3 , 戌 , 交友 , seq));
    assertSame(戌 , ZiweiIF.getHouseBranch(3 , 戌 , 官祿 , seq));
    assertSame(酉 , ZiweiIF.getHouseBranch(3 , 戌 , 田宅 , seq));
    assertSame(申 , ZiweiIF.getHouseBranch(3 , 戌 , 福德 , seq));
    assertSame(未 , ZiweiIF.getHouseBranch(3 , 戌 , 父母 , seq));
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
    assertSame(丙午 , impl.getHouse(丁 , 3 , 戌 , 命宮 , seq));
    assertSame(乙巳 , impl.getHouse(丁 , 3 , 戌 , 兄弟 , seq));
    assertSame(甲辰 , impl.getHouse(丁 , 3 , 戌 , 夫妻 , seq));
    assertSame(癸卯 , impl.getHouse(丁 , 3 , 戌 , 子女 , seq));
    assertSame(壬寅 , impl.getHouse(丁 , 3 , 戌 , 財帛 , seq));
    assertSame(癸丑 , impl.getHouse(丁 , 3 , 戌 , 疾厄 , seq));
    assertSame(壬子 , impl.getHouse(丁 , 3 , 戌 , 遷移 , seq));
    assertSame(辛亥 , impl.getHouse(丁 , 3 , 戌 , 交友 , seq));
    assertSame(庚戌 , impl.getHouse(丁 , 3 , 戌 , 官祿 , seq));
    assertSame(己酉 , impl.getHouse(丁 , 3 , 戌 , 田宅 , seq));
    assertSame(戊申 , impl.getHouse(丁 , 3 , 戌 , 福德 , seq));
    assertSame(丁未 , impl.getHouse(丁 , 3 , 戌 , 父母 , seq));
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
    assertSame(Branch.寅 , ZiweiIF.getBodyHouseBranch(3 , 戌));

    // 丁酉年 3月14日，子時，身命同宮 , 都在 辰
    assertSame(辰 , ZiweiIF.getMainHouseBranch(3 , Branch.子));
    assertSame(辰 , ZiweiIF.getBodyHouseBranch(3 , Branch.子));
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
    // 假設某個人出生日是23日，五行局為金四局 ==> 紫微在午
    assertSame(午 , getBranchOfPurpleStar(4 , 23));

    // 假設有一個人是24日生，金四局 ==> 紫微在未
    assertSame(未 , getBranchOfPurpleStar(4 , 24));

    // ex：有個人出生年月日是西元 1980年(民69庚申) 農曆 7月23日 丑時 (男) ==> 紫微在 甲申 , 天府也在甲申 (紫微、天府 同宮)
    assertSame(甲申 , impl.getStemBranchBranchOfPurpleStar(庚 , 3 , 23));
    assertSame(甲申 , impl.getStemBranchOfTianFuStar(庚 , 3 , 23));

    // 已知 : 2017(丁酉年)-04-10 (農曆 三月14日） 未時 , 土5局 , 紫微 在 癸卯 , 天府 在 癸丑
    assertSame(癸卯 , impl.getStemBranchBranchOfPurpleStar(丁 , 5 , 14));
    assertSame(癸丑 , impl.getStemBranchOfTianFuStar(丁 , 5 , 14));


    // 已知 : 2017(丁酉年)-04-10 (農曆 三月14日） 酉時 , 水2局 , 紫微、天府 都在戊申
    assertSame(戊申 , impl.getStemBranchBranchOfPurpleStar(丁 , 2 , 14));
    assertSame(戊申 , impl.getStemBranchOfTianFuStar(丁 , 2 , 14));
  }

  @Test
  public void testHouseOf() {
    // 範例 : http://sweeteason.pixnet.net/blog/post/43447102
    // ex：有個人出生年月日是西元 1980年(民69庚申) 農曆 7月23日 丑時 (男) , 木三局 ==> 紫微在 甲申 , 天府也在甲申 (紫微、天府 同宮)
    assertSame(甲申, impl.getStemBranchOf(紫微, 庚, 3, 23));
    assertSame(癸未, impl.getStemBranchOf(天機, 庚, 3, 23));
    assertSame(辛巳, impl.getStemBranchOf(太陽, 庚, 3, 23));
    assertSame(庚辰, impl.getStemBranchOf(武曲, 庚, 3, 23));
    assertSame(己卯, impl.getStemBranchOf(天同, 庚, 3, 23));
    assertSame(戊子, impl.getStemBranchOf(廉貞, 庚, 3, 23));

    assertSame(甲申, impl.getStemBranchOf(天府, 庚, 3, 23));
    assertSame(乙酉, impl.getStemBranchOf(太陰, 庚, 3, 23));
    assertSame(丙戌, impl.getStemBranchOf(貪狼, 庚, 3, 23));
    assertSame(丁亥, impl.getStemBranchOf(巨門, 庚, 3, 23));
    assertSame(戊子, impl.getStemBranchOf(天相, 庚, 3, 23));
    assertSame(己丑, impl.getStemBranchOf(天梁, 庚, 3, 23));
    assertSame(戊寅, impl.getStemBranchOf(七殺, 庚, 3, 23));
    assertSame(壬午, impl.getStemBranchOf(破軍, 庚, 3, 23));
  }
}