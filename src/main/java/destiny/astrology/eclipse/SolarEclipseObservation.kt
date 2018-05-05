/**
 * Created by smallufo on 2017-11-07.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth

/**
 * 某時某刻某地點，觀測到的日食相關資訊
 */
class SolarEclipseObservation(gmtJulDay: Double, lng: Double, lat: Double, alt: Double,
                              /** 食 的種類  */
                              val type: ISolarEclipse.SolarType, azimuth: Azimuth,
  /** 直徑被蓋住的比例  */
                              /** 半徑被蓋住的比例  */
                              val magnitude: Double,
  /** 面積被蓋住的比例  */
                              /** 面積被蓋住的比例  */
                              val obscuration: Double) : AbstractEclipseObservation(gmtJulDay, lng, lat, alt, azimuth) {


  override fun toString(): String {
    return ("[EclipseObservation "
      + " (lat,lng)=" + lat + "," + lng
      + ", azimuth=" + azimuth
      + ", magnitude=" + magnitude
      + ", obscuration=" + obscuration + ']')
  }
}
