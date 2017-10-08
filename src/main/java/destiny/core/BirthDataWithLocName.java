/**
 * @author smallufo
 * Created on 2008/4/1 at 上午 5:43:00
 */
package destiny.core;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.Nullable;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 生日資料
 * + 使用者姓名
 * + 地點名稱
 */
public class BirthDataWithLocName extends BirthData {

  /** 命造姓名 */
  @Nullable
  private String name = null;

  // 地點名稱
  @Nullable
  private String place = null;

  public BirthDataWithLocName(Gender gender, ChronoLocalDateTime time, Location location, @Nullable String name , @Nullable String place) {
    super(gender, time, location);
    this.name = name;
    this.place = place;
  }

  public BirthDataWithLocName(Gender gender, ChronoLocalDateTime chronoLocalDateTime, Location location, @Nullable String name) {
    super(gender, chronoLocalDateTime, location);
    this.name = name;
  }

  @Nullable
  public String getPlace() {
    return place;
  }

  public Optional<String> getOptionalPlace() {
    return Optional.ofNullable(place);
  }

  public void setPlace(String place) {
    this.place = place;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public Optional<String> getOptionalName() {
    return Optional.ofNullable(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof BirthDataWithLocName))
      return false;
    if (!super.equals(o))
      return false;
    BirthDataWithLocName that = (BirthDataWithLocName) o;
    return Objects.equals(name, that.name) && Objects.equals(place, that.place);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name, place);
  }
}
