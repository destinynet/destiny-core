/**
 * Created by smallufo at 2009/3/17 下午 9:44:34
 */
package destiny.tools.location;

import destiny.core.calendar.Location;

import java.util.Locale;

/**
 * 以 {@link IGeocoding} 取代
 */
public interface GeolocationFinder {

  /** 給定一個地址，找出相對應的經緯度等資料 */
  Location getLocation(String place, Locale locale);
}
