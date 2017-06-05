/**
 * @author smallufo
 * Created on 2008/4/1 at 上午 5:43:00
 */
package destiny.core;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
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
  private String name;

  private String locationName = "";

  public BirthDataWithLocName(Gender gender, LocalDateTime time, Location location, @Nullable String name) {
    super(gender, time, location);
    this.name = name;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
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
    return Objects.equals(name, that.name) && Objects.equals(locationName, that.locationName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name, locationName);
  }
}
