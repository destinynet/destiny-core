/** 2009/7/13 上午 4:00:28 by smallufo */
package destiny.IChing.contentProviders;

import java.util.Locale;

import destiny.IChing.HexagramIF;

/** 彖曰 */
public interface HexagramJudgementIF
{
  public String getJudgement(HexagramIF hexagram , Locale locale);
}

