/** 2009/7/13 上午 4:28:41 by smallufo */
package destiny.iching.contentProviders;

import destiny.iching.HexagramIF;

import java.util.Locale;

/** 卦 或 爻 的象曰 */
public interface ImageIF {

  /** 取得卦的象曰 */
  String getHexagramImage(HexagramIF hexagram, Locale locale);

  /** 取得爻的象曰 , lineIndex 亦可直接傳 0 或 7 進來 */
  String getLineImage(HexagramIF hexagram, int lineIndex, Locale locale);

  /** 用九/用六的象曰 */
  String getExtraImage(HexagramIF hexagram, Locale locale);
}

