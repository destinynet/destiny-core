/**
 * Created by smallufo on 2015-05-23.
 */
package destiny.core.chinese.liuren.golden;

import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.StemBranchUtils;

import java.io.Serializable;

/**
 * 六壬金口訣，核心資料結構
 */
public class GoldenMouth implements Serializable {

  /** 八字 */
  private final EightWords ew;

  /** 地分 */
  private final Branch direction;

  /** 月將（太陽星座） */
  private final Branch monthSign;

  /** 貴神 */
  private final StemBranch benefactor;

  public GoldenMouth(EightWords ew, Branch direction, Branch monthSign, StemBranch benefactor) {
    this.ew = ew;
    this.direction = direction;
    this.monthSign = monthSign;
    this.benefactor = benefactor;
  }

  /**
   * 取得「人元」 : 演算法如同「五鼠遁時」法
   * 甲己還是甲 乙庚丙作初
   * 丙辛起戊子 丁壬庚子辰
   * 戊癸壬子頭 時元從子推
   */
  public Stem getHuman() {
    return StemBranchUtils.getHourStem(ew.getDayStem() , direction);
  }

  /** 取得「貴神」 */
  public StemBranch getBenefactor() {
    return benefactor;
  }


  /**
   * 取得「將神」 : 從時辰開始，順數至「地分」
   */
  public StemBranch getJohnson() {
    // 從「地分」領先「時辰」多少
    int steps = direction.getAheadOf(ew.getHourBranch());
    System.out.println("地分 " + direction + " 領先時辰 " + ew.getHourBranch() + "  " + steps + " 步");

    // 接下來，將月將 加上此 step
    Branch branch = monthSign.next(steps);

    Stem stem = StemBranchUtils.getHourStem(ew.getDayStem() , monthSign.next(steps));
    System.out.println("月將 = " + monthSign + " , 加上 " + steps + " 步 , 將神地支 = " + branch + " , 天干為 " + stem);
    return StemBranch.get(stem , branch);
  }


  /**
   * 取得「地分」
   */
  public Branch getDirection() {
    return direction;
  }

  /**
   * 取得八字
   */
  public EightWords getEightWords() {
    return ew;
  }

}
