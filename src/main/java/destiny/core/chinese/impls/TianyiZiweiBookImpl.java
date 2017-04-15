/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.impls;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.TianyiIF;
import destiny.core.chinese.YinYangIF;

import java.io.Serializable;
import java.util.Locale;

import static destiny.core.chinese.Branch.*;

/**
 * 紫微斗數全書 對於天乙貴人的設定
 * 甲戊庚牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，六辛逢馬虎，此是貴人鄉
 *
 * 與 {@link TianyiLiurenPithyImpl} 《大六壬金口訣》《六壬神課金口訣》 差別只在於「壬癸」
 *
 * 截圖 http://imgur.com/1rmn11a
 */
@SuppressWarnings("Duplicates")
public class TianyiZiweiBookImpl implements TianyiIF, Serializable {

  @Override
  public Branch getFirstTianyi(Stem stem, YinYangIF yinYang) {
    switch (stem) {
      case 甲:
      case 戊:
      case 庚:
        return yinYang.getBooleanValue() ? 丑 : 未;

      case 乙:
      case 己:
        return yinYang.getBooleanValue() ? 子 : 申;

      case 丙:
      case 丁:
        return yinYang.getBooleanValue() ? 亥 : 酉;

      case 壬:
      case 癸:
        return yinYang.getBooleanValue() ? 卯 : 巳 ;

      case 辛:
        return yinYang.getBooleanValue() ? 午 : 寅;

      default:
        throw new AssertionError(stem + " at " + yinYang);
    }
  }

  @Override
  public String getTitle(Locale locale) {
    return "《紫微斗數全書》";
  }

  @Override
  public String getDescription(Locale locale) {
    return "《紫微斗數全書》：甲戊庚牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，六辛逢馬虎，此是貴人鄉";
  }
}
