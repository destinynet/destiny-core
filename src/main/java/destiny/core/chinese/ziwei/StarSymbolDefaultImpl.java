/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei;

import destiny.iching.Symbol;

import java.io.Serializable;

import static destiny.iching.Symbol.*;
import static destiny.iching.Symbol.離;

/**
 * 紫微斗數配卦
 *
 * 參考此圖 http://imgur.com/a/hD1tx
 *
 * 其餘系統參考這裡  http://skylight-hk.net/forum/forum.php?mod=viewthread&tid=607
 */
public class StarSymbolDefaultImpl implements StarSymbolIF , Serializable {

  @Override
  public Symbol getSymbolAcquired(ZStar star) {
    switch (star) {
      case 紫微:case 天府: return 艮;
      case 天機:case 巨門: return 震;
      case 貪狼:return 巽;
      case 太陽:case 天相: return 離;
      case 武曲:case 破軍: return 坤;
      case 天同:case 天梁: return 兌;
      case 七殺: return 乾;
      case 廉貞: case 太陰: return 坎;
      default: throw new AssertionError("Error : " + star);
    }
  }
}
