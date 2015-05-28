/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

public interface ClockwiseIF extends Descriptive {

  Clockwise getClockwise(Time lmt, Location loc);
}
