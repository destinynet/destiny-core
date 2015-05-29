/**
 * @author smallufo
 * Created on 2011/4/12 at 上午11:26:31
 */
package destiny.utils.location;

import java.util.Locale;
import java.util.Optional;

public interface ReverseGeocodingService {

  Optional<String> getNearbyLocation(double longitude, double latitude, Locale locale);
}
