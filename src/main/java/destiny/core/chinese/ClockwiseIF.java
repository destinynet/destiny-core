/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public interface ClockwiseIF extends Descriptive {

  Clockwise getClockwise(LocalDateTime lmt, Location loc);
}
