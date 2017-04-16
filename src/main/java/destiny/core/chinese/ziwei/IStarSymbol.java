/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei;

import destiny.iching.Symbol;
import destiny.iching.SymbolAcquired;
import destiny.iching.SymbolCongenital;

/**
 * 紫微斗數配卦
 *
 * 取得此顆星，在 先天八卦 ({@link SymbolCongenital}) / 後天八卦 ({@link SymbolAcquired}) 的方位
 */
public interface IStarSymbol {

  /** 後天八卦 */
  Symbol getSymbolAcquired(StarMain star);

  /** 先天八卦 : 先求後天八卦，再 map 回先天八卦 */
  default Symbol getSymbolCongenital(StarMain star) {
    return getSymbolAcquired(star).toCongential();
  }
}
