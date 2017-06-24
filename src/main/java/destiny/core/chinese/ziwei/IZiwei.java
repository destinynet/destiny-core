/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.YearMonthIF;
import destiny.core.chinese.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static destiny.core.chinese.Branch.子;
import static destiny.core.chinese.Branch.寅;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.StarLucky.*;
import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.core.chinese.ziwei.StarUnlucky.火星;
import static destiny.core.chinese.ziwei.StarUnlucky.鈴星;
import static destiny.core.chinese.ziwei.ZContext.MonthAlgo.MONTH_LEAP_NEXT;
import static destiny.core.chinese.ziwei.ZContext.MonthAlgo.MONTH_LEAP_SPLIT15;
import static destiny.core.chinese.ziwei.ZContext.MonthAlgo.MONTH_SOLAR_TERMS;

/** 紫微斗數 */
public interface IZiwei {

  Logger logger = LoggerFactory.getLogger(IZiwei.class);

  /**
   * @param monthNum       陰曆月份
   * @param leapMonth      是否閏月
   * @param monthBranch    節氣月支
   * @param monthAlgorithm 哪種演算法
   * @param days           日數
   * @return 取得最終要計算的「月份」數字
   */
  static int getFinalMonthNumber(int monthNum , boolean leapMonth , Branch monthBranch , int days , ZContext.MonthAlgo monthAlgorithm) {
    if (monthAlgorithm == MONTH_SOLAR_TERMS) {
      // 節氣盤的話，直接傳回 月支 數(相對於「寅」)
      return monthBranch.getAheadOf(寅) + 1; // 別忘了 +1
    } else {
      int finalMonthNum = monthNum; // 內定為本月
      if (leapMonth) {
        // 若是閏月
        if (monthAlgorithm == MONTH_LEAP_NEXT) {
          // 且設定為「一律當下月」
          finalMonthNum = monthNum+1;
        } else if (monthAlgorithm == MONTH_LEAP_SPLIT15) {
          // 且設定為「月半切割」
          if (days > 15) {
            finalMonthNum = monthNum+1;
          }
        }
      }
      return finalMonthNum;
    }
  }

  /**
   * 命宮 : (月數 , 時支) -> 地支
   * 寅宮開始，順數月數，再逆數時支
   *
   * 假設我的出生月日是農曆 7月6日 巳時，順數生月，那就是從寅宮開始順時針走七步 , 因為是農曆七月，所以經由 順數生月，所以我們找到了申宮
   *
   * 找到申宮之後再 逆數生時 找到了卯宮，所以卯就是你的命宮
   * */
  static Branch getMainHouseBranch(int finalMonthNum, Branch hour) {
    return 寅.next(finalMonthNum - 1).prev(hour.getIndex());
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
  static StemBranch getMainHouse(Stem year, int finalMonthNum, Branch hour) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    Branch branch = getMainHouseBranch(finalMonthNum , hour);
    return getStemBranchOf(branch , stemOf寅);
  } // 取得命宮

  /** 承上 , 找到命宮的 干支 ，可以取得「五行、第幾局」 */
  static Tuple2<FiveElement , Integer> getMainDesc(StemBranch mainHouse) {
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
    return Tuple.tuple(fiveElement , set);
  }


  /**
   * 身宮 (月數 , 時支) -> 地支
   * 順數生月，順數生時 就可以找到身宮
   */
  static Branch getBodyHouseBranch(int finalMonthNum , Branch hour) {
    return 寅.next(finalMonthNum-1).next(hour.getIndex());
  }

  /** 承上， 身宮 的干支 */
  static StemBranch getBodyHouse(Stem year , int finalMonthNum , Branch hour) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    Branch branch = getBodyHouseBranch(finalMonthNum , hour);
    return getStemBranchOf(branch , stemOf寅);
  }

  /**
   * 從命宮開始，逆時針，飛佈 兄弟、夫妻...
   */
  static Branch getHouseBranch(int month, Branch hour, House house, IHouseSeq seq) {
    // 命宮 的地支
    Branch branchOfFirstHouse = getMainHouseBranch(month , hour);
    int steps = seq.getAheadOf(house , House.命宮);
    return branchOfFirstHouse.prev(steps);
  }

  /**
   * 承上 , 取得該宮位的「天干」＋「地支」組合
   */
  @Deprecated
  static StemBranch getHouse(Stem year, int month, Branch hour, House house, IHouseSeq seq) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);
    // 先取得 該宮位的地支
    Branch branch = getHouseBranch(month , hour , house , seq);
    return getStemBranchOf(branch , stemOf寅);
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
      default: throw new AssertionError("Error : " + year);
    }
  }


  /**
   * 計算本命盤
   *
   * @param optionalMainBranch 預先計算過的命宮
   * @param optionalBodyBranch 預先計算過的身宮
   * @param lunarYear          陰曆的年干支
   * @param solarYear          「節氣」的年干支
   * @param lunarMonth         陰曆的月份
   * @param monthBranch        「節氣」的月支
   */
  Builder getBirthPlate(Optional<Branch> optionalMainBranch, Optional<Branch> optionalBodyBranch, int cycle, StemBranch lunarYear, StemBranch solarYear, int lunarMonth, boolean leapMonth, Branch monthBranch, SolarTerms solarTerms, int days, Branch hour, @NotNull Collection<ZStar> stars, Gender gender, ZContext context) ;

  /** 輸入現代化的資料，計算本命盤 */
  Builder getBirthPlate(LocalDateTime lmt, Location location, String place, @NotNull Collection<ZStar> stars, Gender gender, ZContextMore context, SolarTermsIF solarTermsImpl, YearMonthIF yearMonthImpl, DayIF dayImpl);

  /** 計算 大限盤 */
  Builder getFlowBig(Builder builder , ZContext context, StemBranch flowBig) ;


  /** 計算 流年盤 */
  Builder getFlowYear(Builder builder , ZContext context, StemBranch flowBig, StemBranch flowYear) ;

  /** 計算 流月盤 TODO : 流月必須要考慮 「是否閏月」 , 可能要拆成 Integer + Boolean */
  Builder getFlowMonth(Builder builder , ZContext context, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth);

  /** 計算 流日盤 */
  Builder getFlowDay(Builder builder , ZContext context,
                     StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum);

  /** 計算 流時盤 */
  Builder getFlowHour(Builder builder , ZContext context,
                      StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum , StemBranch flowHour);

  /** 流年斗君
   * flowYear -> 流年 , anchor -> 錨 , 意為： 以此為當年度之「定錨」（亦為一月), 推算流月、甚至流日、流時
   * */
  static Branch getFlowYearAnchor(Branch flowYear , int birthMonth , Branch birthHour) {
    return flowYear                     // 以流年地支為起點
      .prev(birthMonth-1)               // 從1 逆數至「出生月」
      .next(birthHour.getAheadOf(子));   // 再順數至「出生時」
  }

  /** 命主 : 命宮所在地支安星 */
  static ZStar getMainStar(Branch branch) {
    switch (branch) {
      case 子: return 貪狼;
      case 丑:case 亥: return 巨門;
      case 寅:case 戌: return 祿存;
      case 卯:case 酉: return 文曲;
      case 辰:case 申: return 廉貞;
      case 巳:case 未: return 武曲;
      case 午:return 破軍;
      default: throw new AssertionError("Error : " + branch);
    }
  }


  /** 身主 : 以出生年之地支安星 */
  static ZStar getBodyStar(Branch branch) {
    switch (branch) {
      case 子: return 火星;
      case 丑:case 未: return 天相;
      case 寅:case 申: return 天梁;
      case 卯:case 酉: return 天同;
      case 辰:case 戌: return 文昌;
      case 巳:case 亥: return 天機;
      case 午: return 鈴星;
      default: throw new AssertionError("Error : " + branch);
    }
  }
}
