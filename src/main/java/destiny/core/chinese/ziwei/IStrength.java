/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Branch;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 廟◎－星曜所在宮位是亮度最為光亮時，稱為入廟；任何星曜以入廟為最佳。
 * 旺○－星曜所在宮位是亮度為次光亮，僅次於「廟」，尚佳，稱之為旺。
 * 利△－星曜所在宮位是亮度無力，處於最黯淡或最弱的狀態，易受其他星曜的影響，若遇吉星可增強其力量，但若逢煞星，則易受到煞星的影響。
 * 陷╳－星曜所在宮位是暗淡無光，易使其缺點顯現出來，遇吉星稍可補救其缺點，如逢煞星則為不吉。
 */
public interface IStrength extends Descriptive {

  /** 取得一個星體，在 12 個宮位的廟旺表 */
  Map<Branch, Integer> getMapOf(ZStar star);

  Optional<Integer> getStrengthOf(ZStar star , Branch branch);

  /**
   *
   *                           1  2       3    4    5      6          7
   * 南派依序分成               →廟、旺、    得地     、平和、   閒地      、陷    ，等六級。
   * 北派依序分成（即紫微斗數全書）→廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
   */
  /** 將 1轉成「廟」 , 2轉成「旺」... */
  static String getString(int value , Locale locale) {
    if (locale == Locale.CHINA) {
      switch (value) {
        case 1 : return "庙";
        case 2 : return "旺";
        case 3 : return "地";
        case 4 : return "利";
        case 5 : return "平";
        case 6 : return "闲";
        case 7 : return "陷";
        default: throw new AssertionError("Error : " + value);
      }
    }
    switch (value) {
      case 1 : return "廟";
      case 2 : return "旺";
      case 3 : return "地";
      case 4 : return "利";
      case 5 : return "平";
      case 6 : return "閒";
      case 7 : return "陷";
      default: throw new AssertionError("Error : " + value);
    }
  }
}
