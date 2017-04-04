/**
 * @author smallufo
 * Created on 2011/4/12 at 上午10:41:20
 */
package destiny.tools.location;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.TimeZone;

public interface TimeZoneService {

  /** 嘗試從經緯度，尋找 TimeZone */
  @NotNull
  Optional<TimeZone> getTimeZoneOptional(double lng , double lat);

  /** 若找不到，傳回 GMT */
  @NotNull
  default TimeZone getTimeZone(double longitude, double latitude) {
    return getTimeZoneOptional(longitude , latitude)
      .orElse(TimeZone.getTimeZone("GMT"));
  }
}
