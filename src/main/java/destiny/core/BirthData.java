/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core;

import destiny.core.calendar.DateIF;
import destiny.core.calendar.Location;
import destiny.core.calendar.LocationIF;
import destiny.core.calendar.TimeIF;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoField.*;

/** 一個命盤最基本的必備元素 : 性別 / 時間 / 地點 */
public class BirthData implements GenderIF, TimeIF, DateIF, LocationIF, Serializable {

  private Gender gender;

  private ChronoLocalDateTime time;

  private Location location;

  public BirthData(Gender gender, ChronoLocalDateTime time, Location location) {
    this.gender = gender;
    this.time = time;
    this.location = location;
  }

  @Override
  public Gender getGender() {
    return gender;
  }

  @Override
  public ChronoLocalDateTime getTime() {
    return time;
  }

  @Override
  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public boolean isAd() {
    return time.get(YEAR) > 0;
  }

  @Override
  public int getYear() {
    return time.get(YEAR_OF_ERA);
  }

  @Override
  public int getMonth() {
    return time.get(MONTH_OF_YEAR);
  }

  @Override
  public int getDay() {
    return time.get(DAY_OF_MONTH);
  }

  public int getHour() {
    return time.get(HOUR_OF_DAY);
  }

  public int getMinute() {
    return time.get(MINUTE_OF_HOUR);
  }

  public double getSecond() {
    return time.get(SECOND_OF_MINUTE) + time.get(NANO_OF_SECOND) / 1_000_000_000.0;
  }

  @Override
  public String toString() {
    return "[BirthData " + "gender=" + gender + ", time=" + time + ", location=" + location + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof BirthData))
      return false;
    BirthData birthData = (BirthData) o;
    return gender == birthData.gender && Objects.equals(time, birthData.time) && Objects.equals(location, birthData.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gender, time, location);
  }
}
