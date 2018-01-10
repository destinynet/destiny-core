/** 2009/7/13 上午 3:40:47 by smallufo */
package destiny.iching.contentProviders;

import destiny.iching.IHexagram;

import java.util.Locale;

/** 取得全名，例如「乾為天」 */
public interface HexagramNameFullIF
{
  /** 取得全名，例如「乾為天」 */
  String getNameFull(IHexagram hexagram , Locale locale);
}

