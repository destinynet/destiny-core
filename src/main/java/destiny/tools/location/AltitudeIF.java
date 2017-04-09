/** 2009/12/7 下午6:24:26 by smallufo */
package destiny.tools.location;

import java.util.Optional;

public interface AltitudeIF {

  /** 從經緯度取得高度 (meters) */
  Optional<Double> getAltitude(double longitude, double latitude) ;
}

