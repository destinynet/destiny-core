/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.util.Map;
import java.util.Optional;

/**
 * 廟◎－星曜所在宮位是亮度最為光亮時，稱為入廟；任何星曜以入廟為最佳。
 * 旺○－星曜所在宮位是亮度為次光亮，僅次於「廟」，尚佳，稱之為旺。
 * 利△－星曜所在宮位是亮度無力，處於最黯淡或最弱的狀態，易受其他星曜的影響，若遇吉星可增強其力量，但若逢煞星，則易受到煞星的影響。
 * 陷╳－星曜所在宮位是暗淡無光，易使其缺點顯現出來，遇吉星稍可補救其缺點，如逢煞星則為不吉。
 */
public interface IStrength {

  /** 取得一個星體，在 12 個宮位的廟旺表 */
  Map<Branch, Integer> getMapOf(ZStar star);

  Optional<Integer> getStrengthOf(ZStar star , Branch branch);
}
