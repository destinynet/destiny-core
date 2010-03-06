package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * @author smallufo
 * @date 2002/8/23
 * @time 下午 07:32:14
 */
public interface EightWordsFactoryIF
{
  /**
   * 由 LMT 取得八字
   */
  public EightWords getEightWords(Time lmt , Location location);
}
