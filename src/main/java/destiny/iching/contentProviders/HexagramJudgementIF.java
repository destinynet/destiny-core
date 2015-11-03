/** 2009/7/13 上午 4:00:28 by smallufo */
package destiny.iching.contentProviders;

import destiny.iching.HexagramIF;

import java.util.Locale;

/** 彖曰 */
public interface HexagramJudgementIF {

  String getJudgement(HexagramIF hexagram, Locale locale);
}

