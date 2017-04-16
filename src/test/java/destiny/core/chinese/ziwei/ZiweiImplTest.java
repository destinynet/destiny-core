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
import static destiny.core.chinese.FiveElement.木;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.StemBranch.*;
import static destiny.core.chinese.ziwei.House.*;
import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.core.chinese.ziwei.IZiwei.getBranchOfPurpleStar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ZiweiImplTest {

  private IHouseSeq seq = new HouseSeqDefaultImpl();

  IZiwei impl = new ZiweiImpl();

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 比對星橋 NCC-907 輸出結果
   * 1989-08-26 辰時
   * 農曆 己巳年 七月25日 辰時
   * 木三局
   * 比對資料 http://chineseypage.com/shop/ncc/ncc907.htm
   * 命盤截圖 http://imgur.com/nnu8s7X
   */
  @Test
  public void testPlate3() {
    Settings settings = new Settings(Tianyi.ZIWEI_BOOK, FireBell.全集, Horse.年馬, HurtAngel.FIXED, Settings.TransFour.DEFAULT);

    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(StarMain.values));
    starList.addAll(Arrays.asList(StarLucky.values));
    starList.addAll(Arrays.asList(StarUnlucky.values));
    starList.addAll(Arrays.asList(StarMinor.values));
    starList.addAll(Arrays.asList(StarDoctor.values));

    Map<ITransFour.Type , Stem> transFourTypes = new ImmutableMap.Builder<ITransFour.Type , Stem>()
      .put(ITransFour.Type.大限 , 甲)
      .put(ITransFour.Type.流年 , 乙)
      .put(ITransFour.Type.流月 , 丙)
      .put(ITransFour.Type.流日 , 丁)
      .put(ITransFour.Type.流時 , 戊)
      .build();

    Plate plate = impl.getPlate(己巳 , 申 , 7 , 25 , 辰 , seq , starList, Gender.女, transFourTypes , settings);

    assertSame(戊辰 , plate.getMainHouse());
    assertSame(丙子 , plate.getBodyHouse());
    assertSame(木 , plate.getFiveElement());
    assertSame(3 , plate.getSet());
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
   * 1970-01-01
   * 民58己酉年 , 十一月24日，子時 , 大驛土 , 水二局（澗下水）
   * 比對 十三行紫微斗數 排盤結果 : http://imgur.com/lcX9Q4o
   * (此圖用的 天使、天傷 是 {@link HurtAngel#FIXED} 版本)
   */
  @Test
  public void testPlate2() {
    Settings settings = new Settings(Tianyi.ZIWEI_BOOK, FireBell.全集, Horse.年馬, HurtAngel.FIXED, Settings.TransFour.DEFAULT);

    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(StarMain.values));
    starList.addAll(Arrays.asList(StarLucky.values));
    starList.addAll(Arrays.asList(StarUnlucky.values));
    starList.addAll(Arrays.asList(StarMinor.values));
    starList.addAll(Arrays.asList(StarDoctor.values));

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
    starList.addAll(Arrays.asList(StarMain.values));
    starList.addAll(Arrays.asList(StarLucky.values));
    starList.addAll(Arrays.asList(StarUnlucky.values));
    starList.addAll(Arrays.asList(StarMinor.values));
    starList.addAll(Arrays.asList(StarDoctor.values));

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
   * 根據此頁面範例 http://www.freehoro.net/ZWDS/Tutorial/PaiPan/19-0_Zi_Liu_NianDouJun.php
   * 如某女士於陽曆1970年10月1日10時10分，為陰曆(庚戌)年9月2日巳時出生，是為陽女。
   * 若現在為西元2012年陰曆過年後(歲次為壬辰)，則為虛歲43歲。
   * 依子年斗君位於酉宮、流年支為辰作為條件，查表得知流年斗君位於丑宮。
   */
  @Test
  public void test流年斗君1() {
    assertSame(丑 , IZiwei.getFlowYearAnchor(辰 , 9 , 巳));
  }

  /**
   * 根據此頁面範例 https://goo.gl/zwWsmO
   * 農曆：(民國)56年11月×日辰時
   * <p>
   * 一個在2002年新暦2月25日・中六合彩的命例:
   * 男:壬午年壬寅月甲子日36歳,大運乙巳
   * 天府天馬同宮,雙祿在辰午二宮夾輔,壬年祿存在亥照會,祿馬同鄕主横財,「斗君子」. // ==> 流年午年 , 斗君在子
   * <p>
   * 農暦正月十四日命宮在丑,
   * 甲子日廉貞在卯福徳宮化祿,
   * 天盤財帛宮在大運流年雙化禄,
   * 此日買中六合彩中齊六個字,發了一筆橫財。
   */
  @Test
  public void test流年斗君2() {
    assertSame(子 , IZiwei.getFlowYearAnchor(午 , 11 , 辰));
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

    assertSame(午 , IZiwei.getMainHouseBranch(3 , 戌));

    assertSame(丙午 , IZiwei.getMainHouse(丁 , 3 , 戌));
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
    assertSame(午 , IZiwei.getHouseBranch(3 , 戌 , 命宮 , seq));
    assertSame(巳 , IZiwei.getHouseBranch(3 , 戌 , 兄弟 , seq));
    assertSame(辰 , IZiwei.getHouseBranch(3 , 戌 , 夫妻 , seq));
    assertSame(卯 , IZiwei.getHouseBranch(3 , 戌 , 子女 , seq));
    assertSame(寅 , IZiwei.getHouseBranch(3 , 戌 , 財帛 , seq));
    assertSame(丑 , IZiwei.getHouseBranch(3 , 戌 , 疾厄 , seq));
    assertSame(子 , IZiwei.getHouseBranch(3 , 戌 , 遷移 , seq));
    assertSame(亥 , IZiwei.getHouseBranch(3 , 戌 , 交友 , seq));
    assertSame(戌 , IZiwei.getHouseBranch(3 , 戌 , 官祿 , seq));
    assertSame(酉 , IZiwei.getHouseBranch(3 , 戌 , 田宅 , seq));
    assertSame(申 , IZiwei.getHouseBranch(3 , 戌 , 福德 , seq));
    assertSame(未 , IZiwei.getHouseBranch(3 , 戌 , 父母 , seq));
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
    assertSame(Branch.寅 , IZiwei.getBodyHouseBranch(3 , 戌));

    // 丁酉年 3月14日，子時，身命同宮 , 都在 辰
    assertSame(辰 , IZiwei.getMainHouseBranch(3 , Branch.子));
    assertSame(辰 , IZiwei.getBodyHouseBranch(3 , Branch.子));
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