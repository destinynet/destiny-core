/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls;

import destiny.astrology.*;
import destiny.core.calendar.Location;

import java.io.Serializable;
import java.util.Locale;

public class DayNightHalfImpl implements DayNightDifferentiator , Serializable {

  private final RiseTransIF riseTransImpl;

  public DayNightHalfImpl(RiseTransIF riseTransImpl) {this.riseTransImpl = riseTransImpl;}

  @Override
  public DayNight getDayNight(double gmtJulDay, Location location) {
    double atmosphericTemperature = 0;
    double atmosphericPressure = 1013.25;
    boolean isDiscCenter = true;
    boolean hasRefraction = true;

    double nextMeridianJulDay = riseTransImpl.getGmtTransJulDay(gmtJulDay , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction);
    double nextNadirJulDay    = riseTransImpl.getGmtTransJulDay(gmtJulDay , Planet.SUN , TransPoint.NADIR    , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction);

    if (nextNadirJulDay > nextMeridianJulDay) {
      //子正到午正（上半天）
      return DayNight.DAY;
    }
    else {
      //午正到子正（下半天）
      return DayNight.NIGHT;
    }
  }


  @Override
  public String getTitle(Locale locale) {
    return "前半天後半天";
  }

  @Override
  public String getDescription(Locale locale) {
    return "夜半子正至午正（前半天）為晝；中午至半夜（後半天）為夜";
  }
}
