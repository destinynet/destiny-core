/** 2009/10/21 上午2:44:14 by smallufo */
package destiny.tools.location;

import destiny.core.calendar.Location;

import java.util.Optional;
import java.util.TimeZone;

/**
 * 從經緯度求 TimeZone
 */
public interface TimeZoneIF
{
  /** 從經緯度查詢 timezone */
  Optional<TimeZone> getTimeZone(double lng, double lat);

  default Optional<TimeZone> getTimeZone(Location.EastWest ew   , int lngDeg , int lngMin , double lngSec ,
                                         Location.NorthSouth nw , int latDeg , int latMin , double latSec) {
    double lng = (ew == Location.EastWest.EAST ? 1 : -1) * (lngDeg + lngMin/60.0 + lngSec/3600.0);
    double lat = (nw == Location.NorthSouth.NORTH ? 1 : -1) * (latDeg + latMin/60.0 + latSec/3600.0);
    return getTimeZone(lng , lat);
  }
}

