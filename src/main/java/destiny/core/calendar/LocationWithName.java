/**
 * Created by smallufo on 2015-05-30.
 */
package destiny.core.calendar;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class LocationWithName implements Serializable {

  private final Location location;

  private final String name;

  public LocationWithName(Location location, String name) {
    this.location = location;
    this.name = name;
  }

  public Location getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  public boolean isDataAvailable() {
    return location != null && StringUtils.isNotBlank(name);
  }

  @Override
  public String toString() {
    return "[ " +
      "location=" + location +
      ", name='" + name + '\'' +
      ']';
  }
}
