/** 2009/7/13 上午 3:58:05 by smallufo */
package destiny.iching.contentProviders;

import java.util.Locale;

import destiny.iching.HexagramIF;

/** 卦辭 */
public interface HexagramExpressionIF
{
  public String getExpression(HexagramIF hexagram , Locale locale);
}

