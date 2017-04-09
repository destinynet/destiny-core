/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static destiny.core.chinese.Branch.寅;
import static destiny.core.chinese.Stem.*;

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
