/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.Clockwise;
import destiny.core.chinese.ClockwiseIF;

import java.io.Serializable;
import java.util.Locale;

/**
 * 固定「晝順夜逆」
 */
public class ClockwiseDefaultImpl implements ClockwiseIF , Descriptive , Serializable {

  private final DayNightDifferentiator differentiator;

  public ClockwiseDefaultImpl(DayNightDifferentiator differentiator) {this.differentiator = differentiator;}

  @Override
  public Clockwise getClockwise(Time lmt, Location loc) {
    DayNight dayNight = differentiator.getDayNight(lmt , loc);
    return (dayNight == DayNight.DAY ? Clockwise.CLOCKWISE : Clockwise.COUNTER);
  }

  @Override
  public String getTitle(Locale locale) {
    return "晝順夜逆";
  }

  @Override
  public String getDescription(Locale locale) {
    return "固定為晝順夜逆";
  }
}
