/** 2009/11/27 下午7:20:26 by smallufo */
package destiny.tools.location;

import destiny.core.calendar.Location;

import java.util.Locale;
import java.util.Optional;

/**
 * 從經緯度尋找附近的地名
 */
public interface ReverseGeocodingIF {

  Optional<String> getNearbyLocation(double lng, double lat, Locale locale);

  default Optional<String> getNearbyLocation(Locale locale ,
    Location.EastWest ew   , int lngDeg , int lngMin , double lngSec ,
    Location.NorthSouth nw , int latDeg , int latMin , double latSec) {
    double lng = (ew == Location.EastWest.EAST ? 1 : -1) * (lngDeg + lngMin/60.0 + lngSec/3600.0);
    double lat = (nw == Location.NorthSouth.NORTH ? 1 : -1) * (latDeg + latMin/60.0 + latSec/3600.0);
    return getNearbyLocation(lng , lat , locale);
  }
}

