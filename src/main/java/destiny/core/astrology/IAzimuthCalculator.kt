/**
 * @author smallufo
 * Created on 2007/5/21 at 上午 5:46:16
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算地平方位角 , 與 Point/Star/Planet/...等星體種類皆無關，只要座標即可
 * 內定實作是 AzimuthImpl
 */
interface IAzimuthCalculator {

  fun IPos.getAzimuth(coordinate: Coordinate,
                      gmtJulDay: GmtJulDay,
                      geoLat: Double,
                      geoLng: Double,
                      geoAlt: Double? = 0.0,
                      temperature: Double = 0.0,
                      pressure: Double = 1013.25): Azimuth {
    return when (coordinate) {
      Coordinate.ECLIPTIC -> getAzimuthFromEcliptic(this, gmtJulDay, geoLat, geoLng, geoAlt, temperature, pressure)
      Coordinate.EQUATORIAL -> getAzimuthFromEquator(this, gmtJulDay, geoLat, geoLng, geoAlt, temperature, pressure)
      /** TODO : 恆星座標系統 [Coordinate.SIDEREAL] 計算 [Azimuth] */
      Coordinate.SIDEREAL -> throw RuntimeException("Not Supported")
    }
  }

  fun IPos.getAzimuth(coordinate: Coordinate,
                      gmtJulDay: GmtJulDay,
                      loc: ILocation,
                      temperature: Double = 0.0,
                      pressure: Double = 1013.25): Azimuth {
    return this.getAzimuth(coordinate, gmtJulDay, loc.lat, loc.lng, loc.altitudeMeter, temperature, pressure)
  }

  /** [Coordinate.ECLIPTIC] 由黃經 , 黃緯 , 求得地平方位角  */
  fun getAzimuthFromEcliptic(eclipticPosition: IPos,
                             gmtJulDay: GmtJulDay,
                             geoLat: Double,
                             geoLng: Double,
                             geoAlt: Double? = 0.0,
                             temperature: Double = 0.0,
                             pressure: Double = 1013.25): Azimuth

  fun getAzimuthFromEcliptic(eclipticPosition: IPos,
                             gmtJulDay: GmtJulDay,
                             location: ILocation,
                             temperature: Double = 0.0,
                             pressure: Double = 1013.25): Azimuth {
    return getAzimuthFromEcliptic(eclipticPosition, gmtJulDay, location.lat, location.lng, location.altitudeMeter,
                                  temperature, pressure)
  }

  /** 承上 , ChronoLocalDateTime 版本  */
  fun getAzimuthFromEcliptic(eclipticPosition: IPos,
                             gmt: ChronoLocalDateTime<*>,
                             location: ILocation,
                             temperature: Double = 0.0,
                             pressure: Double = 1013.25): Azimuth {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getAzimuthFromEcliptic(eclipticPosition, gmtJulDay, location, temperature, pressure)
  }

  /** [Coordinate.EQUATORIAL] 由赤經 , 赤緯 , 求得地平方位角  */
  fun getAzimuthFromEquator(equatorPosition: IPos,
                            gmtJulDay: GmtJulDay,
                            geoLat: Double,
                            geoLng: Double,
                            geoAlt: Double? = 0.0,
                            temperature: Double = 0.0,
                            pressure: Double = 1013.25): Azimuth

  fun getAzimuthFromEquator(equatorPosition: IPos,
                            gmtJulDay: GmtJulDay,
                            location: ILocation,
                            temperature: Double = 0.0,
                            pressure: Double = 1013.25): Azimuth {
    return getAzimuthFromEquator(equatorPosition, gmtJulDay, location.lat, location.lng, location.altitudeMeter,
                                 temperature, pressure)
  }

}
