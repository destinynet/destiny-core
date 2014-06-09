/** 2009/11/27 下午7:20:26 by smallufo */
package destiny.utils.location;

import java.util.Locale;
import java.util.Optional;

/**
 * 從經緯度尋找附近的地名
 */
public interface ReverseGeocodingIF
{
  public Optional<String> getNearbyLocation(double longitude , double latitude , Locale locale);
}

