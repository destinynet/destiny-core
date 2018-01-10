/** 2009/7/13 上午 3:35:07 by smallufo */
package destiny.iching.contentProviders;

import destiny.iching.IHexagram;

import java.util.Locale;

/** 取得「短」的卦名，例如「乾」 */
public interface HexagramNameShortIF {

  /** 取得「短」的卦名，例如「乾」 */
  String getNameShort(IHexagram hexagram, Locale locale);

  /** 從卦的「短卦名」，反查回 Hexagram */
  IHexagram getHexagram(String name, Locale locale);

}

