/**
 * @author smallufo
 * Created on 2008/4/1 at 上午 5:43:00
 */
package destiny.core;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
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

  public BirthDataWithLocName(Gender gender, LocalDateTime time, Location location, String name) {
    super(gender, time, location);
    this.name = name;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Optional<String> getNameOptional() {
    return Optional.ofNullable(name);
  }

  @Nullable
  public String getName() {
    return name;
  }

}
