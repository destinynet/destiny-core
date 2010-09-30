/** 2009/7/13 上午 4:03:01 by smallufo */
package destiny.IChing.contentProviders;

import java.util.Locale;

import destiny.IChing.HexagramIF;

/** 384爻辭 + 用九/用六 兩個 extra */
public interface LineExpressionIF
{
  public String getLineExpression(HexagramIF hexagram , int line , Locale locale);
  public String getExtraExpression(HexagramIF hexagram , Locale locale);
}

