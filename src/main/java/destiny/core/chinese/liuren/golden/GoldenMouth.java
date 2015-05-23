/**
 * Created by smallufo on 2015-05-23.
 */
package destiny.core.chinese.liuren.golden;

import destiny.core.chinese.EarthlyBranches;

import java.io.Serializable;
import java.util.List;

/**
 * 六壬金口訣，核心資料結構
 */
public class GoldenMouth implements Serializable {

  /** 將神（太陽星座） */
  private EarthlyBranches monthSign;

  /** 貴神 */
  private List<EarthlyBranches> benefactors;


  /** 取得「月將」的中文稱謂
   *
   * @see <a href="http://zh.wikipedia.org/wiki/十二月將">十二月將</a>
   * */
  public static String getName(EarthlyBranches branch) {
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
