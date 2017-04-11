/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.chinese.*;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

import static destiny.core.chinese.Branch.寅;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.MainStar.*;

/** 紫微斗數 */
public interface ZiweiIF {

  static Logger logger = LoggerFactory.getLogger(ZiweiIF.class);

  /**
   * 命宮
   *
   * 假設我的出生月日是農曆 7/6 巳時，順數生月，那就是從寅宮開始順時針走七步 , 因為是農曆七月，所以經由 順數生月，所以我們找到了申宮
   *
   * 找到申宮之後再 逆數生時 找到了卯宮，所以卯就是你的命宮
   * */
  default Branch getMainHouseBranch(int month , Branch hour) {
    return 寅.next(month-1).prev(hour.getIndex());
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
  default StemBranch getMainHouse(Stem year , int month , Branch hour) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    Branch mainHouse = getMainHouseBranch(month , hour);
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);

    int steps = mainHouse.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
  }

  /** 承上 , 找到命宮的 干支 ，可以取得「納音、五行、第幾局」 */
  default Tuple3<String , FiveElement , Integer> getNaYin(Stem year , int month , Branch hour) {
    StemBranch mainHouse = getMainHouse(year , month , hour);

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
   * 身宮
   * 順數生月，順數生時 就可以找到身宮
   */
  default Branch getBodyHouse(int month , Branch hour) {
    return 寅.next(month-1).next(hour.getIndex());
  }

  /**
   * 從命宮開始，逆時針，飛佈 兄弟、夫妻...
   */
  default Branch getHouseBranch(int month , Branch hour , House house , HouseSeqIF seq) {
    // 命宮 的地支
    Branch branchOfFirstHouse = getMainHouseBranch(month , hour);
    int steps = seq.getAheadOf(house , House.命宮);
    return branchOfFirstHouse.prev(steps);
  }

  /**
   * 承上 , 取得該宮位的「天干」＋「地支」組合
   */
  default StemBranch getHouse(Stem year , int month , Branch hour , House house , HouseSeqIF seq) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);
    // 先取得 該宮位的地支
    Branch houseBranch = getHouseBranch(month , hour , house , seq);
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);

    int steps = houseBranch.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
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
    Branch purpleBranch = getBranchOfPurpleStar(set , day);
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);

    int steps = purpleBranch.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
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
    Branch tianFuStar = getBranchOfTianFuStar(set , day);
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);

    int steps = tianFuStar.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
  }


  /**
   * 14顆主星 => 地支 的 function mapping
   * TODO : should be private map in Java9
   */
  Map<MainStar , Function<Tuple2<Integer , Integer>, Branch>> mainStar2BranchMap =
      ImmutableMap.<MainStar , Function<Tuple2<Integer , Integer> , Branch>>builder()
        .put(紫微 , t2 -> getBranchOfPurpleStar(t2.v1() , t2.v2()))
        .put(天機 , t2 -> getBranchOf(紫微 , t2.v1() , t2.v2()).prev(1))
        .put(太陽 , t2 -> getBranchOf(紫微 , t2.v1() , t2.v2()).prev(3))
        .put(武曲 , t2 -> getBranchOf(紫微 , t2.v1() , t2.v2()).prev(4))
        .put(天同 , t2 -> getBranchOf(紫微 , t2.v1() , t2.v2()).prev(5))
        .put(廉貞 , t2 -> getBranchOf(紫微 , t2.v1() , t2.v2()).prev(8))

        .put(天府 , t2 -> getBranchOfTianFuStar(t2.v1() , t2.v2()))
        .put(太陰 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(1))
        .put(貪狼 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(2))
        .put(巨門 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(3))
        .put(天相 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(4))
        .put(天梁 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(5))
        .put(七殺 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(6))
        .put(破軍 , t2 -> getBranchOf(天府 , t2.v1() , t2.v2()).next(10))
      .build();

  /**
   * 取得某個主星，位於宮位的地支
   * @param star 14顆主星
   */
  static Branch getBranchOf(MainStar star , int set , int day) {
    return mainStar2BranchMap.get(star).apply(Tuple.tuple(set , day));
  }

  /**
   * 承上，取得某個主星，位於宮位的干支
   * @param star 14顆主星
   */
  default StemBranch getStemBranchOf(MainStar star , Stem year , int set , int day) {
    // 寅 的天干
    Stem stemOf寅 = getStemOf寅(year);

    // 星星的地支
    Branch branch = getBranchOf(star , set , day);
    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);
    int steps = branch.getAheadOf(寅);
    return stemBranchOf寅.next(steps);
  }




  // TODO : should be private after Java9
  default Stem getStemOf寅(Stem year) {
    Stem stemOf寅;
    switch (year) {
      case 甲: case 己: stemOf寅 = 丙; break;
      case 乙: case 庚: stemOf寅 = 戊; break;
      case 丙: case 辛: stemOf寅 = 庚; break;
      case 丁: case 壬: stemOf寅 = 壬; break;
      case 戊: case 癸: stemOf寅 = 甲; break;
      default: throw new RuntimeException("impossible");
    }
    return stemOf寅;
  }

  void calculate(Gender gender , LocalDateTime time , Location loc);
}
