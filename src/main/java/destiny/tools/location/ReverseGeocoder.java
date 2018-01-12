/** 2009/11/27 下午7:20:26 by smallufo */
package destiny.tools.location;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

/**
 * 從經緯度尋找附近的地名
 */
public interface ReverseGeocoder {

  @Nullable
  String getNearbyLocation(double lng, double lat, Locale locale);

  default Optional<String> getNearbyLocationOpt(double lng, double lat, Locale locale) {
    return Optional.ofNullable(getNearbyLocation(lng , lat , locale));
  }

  default Optional<String> getNearbyLocationOpt(Locale locale ,
    Location.EastWest ew   , int lngDeg , int lngMin , double lngSec ,
    Location.NorthSouth nw , int latDeg , int latMin , double latSec) {

    double lng = Location.getLongitude(ew , lngDeg , lngMin , lngSec);
    double lat = Location.getLatitude(nw , latDeg , latMin , latSec);
    return getNearbyLocationOpt(lng , lat , locale);
  }
}

