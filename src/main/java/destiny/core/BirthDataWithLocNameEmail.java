/**
 * @author smallufo
 * Created on 2009/3/24 at 上午 10:23:54
 */
package destiny.core;

import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public class BirthDataWithLocNameEmail extends BirthDataWithLocName {

  private String name;

  private String email;

  public BirthDataWithLocNameEmail(String name, Gender gender, LocalDateTime time, Location location) {
    super(name, gender, time, location);
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getName() {
    return name;
  }

}
