/**
 * @author smallufo
 * Created on 2009/3/24 at 上午 10:23:54
 */
package destiny.core;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.Nullable;

import java.time.chrono.ChronoLocalDateTime;

public class BirthDataWithPlaceEmail extends BirthDataWithPlace {

  @Nullable
  private String email;

  public BirthDataWithPlaceEmail(Gender gender, ChronoLocalDateTime time, Location location, String name) {
    super(gender, time, location, name);
  }

  @Nullable
  public String getEmail() {
    return email;
  }

}
