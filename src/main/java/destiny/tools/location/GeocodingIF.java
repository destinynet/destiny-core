/** 2009/11/26 上午10:38:15 by smallufo */
package destiny.tools.location;

import destiny.core.calendar.Location;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.TimeZone;

/** 從地名尋找經緯度 */
public interface GeocodingIF {

  Optional<Pair<Double, Double>> getLongLat(String placeName) ;

  default Optional<Location> getLocation(String placeName , TimeZoneService timeZoneService) {
    return getLongLat(placeName).map(pair -> {
      double lng = pair.getLeft();
      double lat = pair.getRight();
      TimeZone tz = timeZoneService.getTimeZone(lng, lat);
      return new Location(lng , lat , tz);
    });
  }
}

