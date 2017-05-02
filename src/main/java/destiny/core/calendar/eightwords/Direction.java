/**
 * Created by smallufo on 2014-12-01.
 */
package destiny.core.calendar.eightwords;

import destiny.core.Descriptive;

import java.util.Locale;

/** 排列方向：右到左，還是左到右 */
public enum Direction implements Descriptive {
  R2L, L2R;

  @Override
  public String getTitle(Locale locale) {
    switch (this) {
      case L2R: return "左至右";
      case R2L: return "右至左";
      default: throw new AssertionError("Error : " + this);
    }
  }

  @Override
  public String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
