/**
 * Created by smallufo on 2017-09-19.
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public interface HoroscopeContextIF {

  HoroscopeIF getHoroscope();

  @NotNull
  LocalDateTime getLmt();

  @NotNull
  LocalDateTime getGmt();

  @NotNull
  Location getLocation();
}
