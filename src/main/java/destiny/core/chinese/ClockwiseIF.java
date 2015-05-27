/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

public interface ClockwiseIF {

  Clockwise getClockwise(Time lmt, Location loc);
}
