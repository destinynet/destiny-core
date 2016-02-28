/** 2009/11/26 上午10:38:15 by smallufo */
package destiny.tools.location;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Optional;

/** 從地名尋找經緯度 */
public interface GeocodingIF {

  Optional<Pair<Double, Double>> getLongLat(String placeName) throws IOException;
}

