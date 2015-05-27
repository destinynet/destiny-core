/**
 * Created by smallufo on 2015-05-23.
 */
package destiny.core.chinese.liuren.golden;

import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.StemBranchUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 六壬金口訣，核心資料結構
 */
public class GoldenMouth implements Serializable {

  /** 八字 */
  @NotNull
  private final EightWords ew;

  /** 地分 */
  private final Branch selected;

  /** 月將（太陽星座） */
  private final Branch monthSign;

  /** 貴神 */
  private final StemBranch benefactor;

  public GoldenMouth(EightWords ew, Branch selected, Branch monthSign, StemBranch benefactor) {
    this.ew = ew;
    this.selected = selected;
    this.monthSign = monthSign;
    this.benefactor = benefactor;
  }

  /**
   * 取得「將神」 : 從時辰開始，順數至「地分」
   */
  public StemBranch getJohnson() {
    // 從「地分」領先「時辰」多少
    int steps = selected.getAheadOf(ew.getHourBranch());
    // 接下來，將月將 加上此 step
    Branch branch = monthSign.next(steps);
    Stem stem = StemBranchUtils.getHourStem(ew.getDayStem() , monthSign.next(steps));
    return StemBranch.get(stem , branch);
  }

  /**
   * 取得「人元」 : 演算法如同「五鼠遁時」法
   * 甲己還是甲 乙庚丙作初
   * 丙辛起戊子 丁壬庚子辰
   * 戊癸壬子頭 時元從子推
   */
  public Stem getHuman() {
    return StemBranchUtils.getHourStem(ew.getDayStem() , selected);
  }

  /** 取得「月將」的中文稱謂
   *
   * @see <a href="http://zh.wikipedia.org/wiki/十二月將">十二月將</a>
   * */
  public static String getName(Branch branch) {
    switch (branch) {
      case 子 : return "神後";
      case 丑 : return "大吉";
      case 寅 : return "宮曹";
      case 卯 : return "太沖";
      case 辰 : return "天罡";
      case 巳 : return "太乙";
      case 午 : return "勝光";
      case 未 : return "小吉";
      case 申 : return "傳送";
      case 酉 : return "從魁";
      case 戌 : return "河魁";
      case 亥 : return "登明";
      default: throw new AssertionError("error : " + branch);
    }
  }

}
