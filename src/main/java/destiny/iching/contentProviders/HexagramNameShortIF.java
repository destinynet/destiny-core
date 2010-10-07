/** 2009/7/13 上午 3:35:07 by smallufo */
package destiny.iching.contentProviders;

import java.util.Locale;

import destiny.iching.HexagramIF;

/** 取得「短」的卦名，例如「乾」 */
public interface HexagramNameShortIF
{
  /** 取得「短」的卦名，例如「乾」 */
  public String getNameShort(HexagramIF hexagram , Locale locale);

}

