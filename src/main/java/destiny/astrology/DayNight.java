/**
 * @author smallufo 
 * Created on 2007/12/11 at 下午 11:29:14
 */ 
package destiny.astrology;

import destiny.core.chinese.YinYangIF;

public enum DayNight implements YinYangIF {

  /** 日 */
  DAY(true),

  /** 夜 */
  NIGHT(false);

  private final boolean value;

  DayNight(boolean value) {this.value = value;}

  @Override
  public boolean getBooleanValue() {
    return value;
  }
}
