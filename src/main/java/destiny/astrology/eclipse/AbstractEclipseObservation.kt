/**
 * Created by smallufo on 2017-11-15.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth

import java.io.Serializable

/**
 * 某時某地點，針對某日食、月食的觀測資料
 * [IEclipseObservation]
 */
@Deprecated("")
abstract class AbstractEclipseObservation protected constructor(
  /** 當下的時間點為何  */
  val gmtJulDay: Double,
  /** 經度  */
  val lng: Double,
  /** 緯度  */
  val lat: Double,
  /** 高度 (米)  */
  val alt: Double,
  /** 地平方位角  */
  val azimuth: Azimuth) : Serializable
