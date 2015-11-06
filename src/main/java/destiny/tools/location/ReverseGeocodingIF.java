/** 2009/11/27 下午7:20:26 by smallufo */
package destiny.tools.location;

import java.util.Locale;
import java.util.Optional;

/**
 * 從經緯度尋找附近的地名
 */
public interface ReverseGeocodingIF {

  Optional<String> getNearbyLocation(double lng, double lat, Locale locale);
}

