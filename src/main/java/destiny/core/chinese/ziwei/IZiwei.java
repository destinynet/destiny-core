/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.calendar.eightwords.MonthIF;
import destiny.core.chinese.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static destiny.core.chinese.Branch.子;
import static destiny.core.chinese.Branch.寅;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.StarMain.*;

/** 紫微斗數 */
public interface IZiwei {

  static Logger logger = LoggerFactory.getLogger(IZiwei.class);

  /**
   * 命宮 : (月數 , 時支) -> 地支
   * 寅宮開始，順數月數，再逆數時支
   *
   * 假設我的出生月日是農曆 7月6日 巳時，順數生月，那就是從寅宮開始順時針走七步 , 因為是農曆七月，所以經由 順數生月，所以我們找到了申宮
   *
   * 找到申宮之後再 逆數生時 找到了卯宮，所以卯就是你的命宮
   * */
  static Branch getMainHouseBranch(int month , Branch hour , SolarTerms solarTerms , IMainHouse mainHouseImpl) {
    return mainHouseImpl.getMainHouse(month , hour , solarTerms);
    //return 寅.next(month-1).prev(hour.getIndex());
  }

  /**
   * 承上 , 以五虎遁，定寅宮的天干，然後找到命宮天干
   *
   * 甲年 or 己年生 = 丙寅宮
   * 乙年 or 庚年生 = 戊寅宮
   * 丙年 or 辛年生 = 庚寅宮
   * 丁年 or 壬年生 = 壬寅宮
   * 戊年 or 癸年生 = 甲寅宮
   */
  static StemBranch getMainHouse(Stem year , int month , Branch hour , SolarTerms solarTerms , IMainHouse mainHouseImpl) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    Branch mainHouse = getMainHouseBranch(month , hour , solarTerms , mainHouseImpl);
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);

    int steps = mainHouse.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
  } // 取得命宮

  /** 承上 , 找到命宮的 干支 ，可以取得「納音、五行、第幾局」 */
  static Tuple3<String , FiveElement , Integer> getNaYin(StemBranch mainHouse) {
    String 納音 = NaYin.getDesc(mainHouse);
    // 五行
    FiveElement fiveElement = NaYin.getFiveElement(mainHouse);
    // 第幾局
    int set;
    switch (fiveElement) {
      case 水: set = 2; break;
      case 土: set = 5; break;
      case 木: set = 3; break;
      case 火: set = 6; break;
      case 金: set = 4; break;
      default: throw new AssertionError("impossible");
    }
    return Tuple.tuple(納音 , fiveElement , set);
  }



  /**
   * 身宮 (月數 , 時支) -> 地支
   * 順數生月，順數生時 就可以找到身宮
   */
  static Branch getBodyHouseBranch(int month , Branch hour) {
    return 寅.next(month-1).next(hour.getIndex());
  }

  /** 承上， 身宮 的干支 */
  static StemBranch getBodyHouse(Stem year , int month , Branch hour) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    Branch branch = getBodyHouseBranch(month , hour);
    return getStemBranchOf(branch , stemOf寅);
  }

  /**
   * 從命宮開始，逆時針，飛佈 兄弟、夫妻...
   */
  static Branch getHouseBranch(int month, Branch hour, House house, IHouseSeq seq, SolarTerms solarTerms, IMainHouse mainHouseImpl) {
    // 命宮 的地支
    Branch branchOfFirstHouse = getMainHouseBranch(month , hour , solarTerms , mainHouseImpl);
    int steps = seq.getAheadOf(house , House.命宮);
    return branchOfFirstHouse.prev(steps);
  }

  /**
   * 承上 , 取得該宮位的「天干」＋「地支」組合
   */
  default StemBranch getHouse(Stem year, int month, Branch hour, House house, IHouseSeq seq, SolarTerms solarTerms, IMainHouse mainHouseImpl) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);
    // 先取得 該宮位的地支
    Branch branch = getHouseBranch(month , hour , house , seq , solarTerms , mainHouseImpl);
    return getStemBranchOf(branch , stemOf寅);
  }


  /**
   * 從「寅宮」，「順數」幾步到「紫微星」？
   * 也相等於：
   * 從「寅宮」，「逆數」幾步到「天府星」？
   */
  static int getPurpleSteps(int set , int day) {
    int multiple = day / set;
    logger.debug("{} / {} = {}" , day , set , multiple);
    if (day % set > 0) {
      multiple++;
      logger.debug("multiple ++ , new multiple = {}", multiple);
    }

    // 差數
    int diff = multiple * set - day;

    int steps;
    if (diff % 2 == 1) {
      // 奇數
      steps = multiple - diff;
    } else {
      // 偶數
      steps = multiple + diff;
    }
    return steps;
  }

  /**
   * 取得「紫微星」的「地支」
   *
   * 得出命造五行局後，推判幾倍的命造五行局數可以大於生日數
   * （例如：十六日生人木三局者則六倍，商數+1,得可大與生日數）；
   * 下一步判斷得出來的倍數與生日數之差數（(商數+1）*五行局數-生日數)，再判斷此差數為奇數或偶數；
   *    若差數為奇數，則以倍數減去差數得到一個新的數字；
   *    若差數為偶數，則倍數與差數相加而得一新的數字，
   * 下一步起寅宮並順時針數到上一步驟得出的數目，此一落宮點便是紫微星的位置；
   *
   * @param set 五行局
   * @param day 生日
   */
  static Branch getBranchOfPurpleStar(int set , int day) {
    int steps = getPurpleSteps(set , day);
    return 寅.next(steps-1);
  }

  /** 承上 , 求得紫微星 的天干 + 地支 */
  default StemBranch getStemBranchBranchOfPurpleStar(Stem year , int set , int day) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    // 紫微 地支
    Branch branch = getBranchOfPurpleStar(set , day);
    return getStemBranchOf(branch , stemOf寅);
  }

  /**
   * 天府星 地支
   */
  static Branch getBranchOfTianFuStar(int set , int day) {
    int steps = getPurpleSteps(set , day);
    return 寅.prev(steps-1);
  }

  /**
   * 承上 , 天府星 的天干 + 地支
   */
  default StemBranch getStemBranchOfTianFuStar(Stem year , int set , int day) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    // 天府 地支
    Branch branch = getBranchOfTianFuStar(set , day);
    return getStemBranchOf(branch , stemOf寅);
  }

  /**
   * 取得某個主星，位於宮位的地支
   * @param star 14顆主星
   */
  static Branch getBranchOf(StarMain star , int set , int day) {
    return mainStar2BranchMap.get(star).apply(Tuple.tuple(set , day));
  }

  /**
   * 承上，取得某個主星，位於宮位的干支
   * @param star 14顆主星
   */
  default StemBranch getStemBranchOf(StarMain star , Stem year , int set , int day) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    // 星星的地支
    Branch branch = getBranchOf(star , set , day);
    return getStemBranchOf(branch , stemOf寅);
//    // 左下角，寅宮 的 干支
//    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);
//    int steps = branch.getAheadOf(寅);
//    return stemBranchOf寅.next(steps);
  }

  static StemBranch getStemBranchOf(Branch branch , Stem stemOf寅) {
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);
    int steps = branch.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
  }

  /**
   * 左下角，寅宮 的天干
   * TODO : should be private after Java9
   */
  static Stem getStemOf寅(Stem year) {
    switch (year) {
      case 甲: case 己: return 丙;
      case 乙: case 庚: return 戊;
      case 丙: case 辛: return 庚;
      case 丁: case 壬: return 壬;
      case 戊: case 癸: return 甲;
      default: throw new RuntimeException("impossible");
    }
  }

  /**
   * 14顆主星
   * (局數,生日) -> 地支
   */
  BiFunction<Integer, Integer, Branch> fun紫微 = IZiwei::getBranchOfPurpleStar;
  BiFunction<Integer, Integer, Branch> fun天機 = (set, day) -> fun紫微.apply(set, day).prev(1);
  BiFunction<Integer, Integer, Branch> fun太陽 = (set, day) -> fun紫微.apply(set, day).prev(3);
  BiFunction<Integer, Integer, Branch> fun武曲 = (set, day) -> fun紫微.apply(set, day).prev(4);
  BiFunction<Integer, Integer, Branch> fun天同 = (set, day) -> fun紫微.apply(set, day).prev(5);
  BiFunction<Integer, Integer, Branch> fun廉貞 = (set, day) -> fun紫微.apply(set, day).prev(8);
  BiFunction<Integer, Integer, Branch> fun天府 = IZiwei::getBranchOfTianFuStar;
  BiFunction<Integer, Integer, Branch> fun太陰 = (set, day) -> fun天府.apply(set, day).next(1);
  BiFunction<Integer, Integer, Branch> fun貪狼 = (set, day) -> fun天府.apply(set, day).next(2);
  BiFunction<Integer, Integer, Branch> fun巨門 = (set, day) -> fun天府.apply(set, day).next(3);
  BiFunction<Integer, Integer, Branch> fun天相 = (set, day) -> fun天府.apply(set, day).next(4);
  BiFunction<Integer, Integer, Branch> fun天梁 = (set, day) -> fun天府.apply(set, day).next(5);
  BiFunction<Integer, Integer, Branch> fun七殺 = (set, day) -> fun天府.apply(set, day).next(6);
  BiFunction<Integer, Integer, Branch> fun破軍 = (set, day) -> fun天府.apply(set, day).next(10);


  /**
   * 14顆主星 => 地支 的 function mapping
   * Tuple2<局數 , 生日>
   * TODO : should be private map in Java9
   */
  Map<StarMain, Function<Tuple2<Integer , Integer>, Branch>> mainStar2BranchMap =
      ImmutableMap.<StarMain, Function<Tuple2<Integer , Integer> , Branch>>builder()
        .put(紫微, t2 -> fun紫微.apply(t2.v1(), t2.v2()))
        .put(天機, t2 -> fun天機.apply(t2.v1(), t2.v2()))
        .put(太陽, t2 -> fun太陽.apply(t2.v1(), t2.v2()))
        .put(武曲, t2 -> fun武曲.apply(t2.v1(), t2.v2()))
        .put(天同, t2 -> fun天同.apply(t2.v1(), t2.v2()))
        .put(廉貞, t2 -> fun廉貞.apply(t2.v1(), t2.v2()))
        .put(天府, t2 -> fun天府.apply(t2.v1(), t2.v2()))
        .put(太陰, t2 -> fun太陰.apply(t2.v1(), t2.v2()))
        .put(貪狼, t2 -> fun貪狼.apply(t2.v1(), t2.v2()))
        .put(巨門, t2 -> fun巨門.apply(t2.v1(), t2.v2()))
        .put(天相, t2 -> fun天相.apply(t2.v1(), t2.v2()))
        .put(天梁, t2 -> fun天梁.apply(t2.v1(), t2.v2()))
        .put(七殺, t2 -> fun七殺.apply(t2.v1(), t2.v2()))
        .put(破軍, t2 -> fun破軍.apply(t2.v1(), t2.v2()))
      .build();


  /**
   * 計算本命盤  */
  Plate.Builder getBirthPlate(StemBranch year, int monthNum, boolean leapMonth, Branch monthBranch, SolarTerms solarTerms, int days, Branch hour, @NotNull Collection<ZStar> stars, Gender gender, Settings settings) ;

  /** 輸入現代化的資料，計算本命盤 */
  Plate.Builder getBirthPlate(LocalDateTime lmt, Location location, String place, @NotNull Collection<ZStar> stars, Gender gender, Settings settings, ChineseDateIF chineseDateImpl, SolarTermsIF solarTermsImpl, MonthIF monthImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi);

  /** 計算 大限盤 */
  Plate.Builder getFlowBig(Plate.Builder builder , Settings settings, StemBranch flowBig) ;

  /** 計算 流年盤 */
  Plate.Builder getFlowYear(Plate.Builder builder , Settings settings, StemBranch flowBig, StemBranch flowYear) ;

  /** 計算 流月盤 */
  Plate.Builder getFlowMonth(Plate.Builder builder , Settings settings,
                             StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth);

  /** 計算 流日盤 */
  Plate.Builder getFlowDay(Plate.Builder builder , Settings settings,
                           StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum);

  /** 計算 流時盤 */
  Plate.Builder getFlowHour(Plate.Builder builder , Settings settings,
                            StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum , StemBranch flowHour);


  /** 計算流月命宮 */
  default Branch getFlowMonth(Branch flowYear , Branch flowMonth , int birthMonth , Branch birthHour , IFlowMonth impl) {
    return impl.getFlowMonth(flowYear , flowMonth, birthMonth , birthHour);
  }

  /** 流年斗君
   * flowYear -> 流年 , anchor -> 錨 , 意為： 以此為當年度之「定錨」（亦為一月), 推算流月、甚至流日、流時
   * */
  static Branch getFlowYearAnchor(Branch flowYear , int birthMonth , Branch birthHour) {
    return flowYear                     // 以流年地支為起點
      .prev(birthMonth-1)               // 從1 逆數至「出生月」
      .next(birthHour.getAheadOf(子));   // 再順數至「出生時」
  }
}
