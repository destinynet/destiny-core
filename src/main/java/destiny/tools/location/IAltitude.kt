/** 2009/12/7 下午6:24:26 by smallufo  */
package destiny.tools.location

import java.util.*

interface IAltitude {

  /** 從經緯度取得高度 (meters)  */
  fun getAltitude(longitude: Double, latitude: Double): Double?

  fun getAltitudeOpt(longitude: Double, latitude: Double): Optional<Double> {
    return Optional.ofNullable(getAltitude(longitude , latitude))
  }
}

