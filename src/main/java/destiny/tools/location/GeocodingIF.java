/** 2009/11/26 上午10:38:15 by smallufo */
package destiny.tools.location;

import destiny.core.calendar.Location;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.TimeZone;

/** 從地名尋找經緯度 */
public interface GeocodingIF {

  Optional<Tuple2<Double, Double>> getLongLat(String placeName) ;

  default Optional<Location> getLocation(String placeName , TimeZoneService timeZoneService) {
    return getLongLat(placeName).map(pair -> {
      double lng = pair.v1();
      double lat = pair.v2();
      TimeZone tz = timeZoneService.getTimeZone(lng, lat);
      return new Location(lng , lat , tz);
    });
  }
}

