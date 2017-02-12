/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.core.calendar.Location;
import destiny.core.chinese.Clockwise;
import destiny.core.chinese.ClockwiseIF;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * 推算貴神
 * 固定「晝順夜逆」
 * Clockwise Day Clockwise / Night Counter
 * dayNightFixedImpl
 */
public class ClockwiseDayNightFixedImpl implements ClockwiseIF , Serializable {

  private final DayNightDifferentiator differentiator;

  public ClockwiseDayNightFixedImpl(DayNightDifferentiator differentiator) {this.differentiator = differentiator;}

  @Override
  public Clockwise getClockwise(LocalDateTime lmt, Location loc) {
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
