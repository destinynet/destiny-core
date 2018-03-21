/**
 * @author smallufo
 * Created on 2007/5/21 at 上午 5:46:16
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime

/**
 * 計算地平方位角 , 與 Point/Star/Planet/...等星體種類皆無關，只要座標即可
 * 內定實作是 AzimuthImpl
 */
interface IAzimuth {

  /** 由黃經 , 黃緯 , 求得地平方位角  */
  fun getAzimuthFromEcliptic(eclipticPosition: IPos, gmtJulDay: Double, geoLng: Double, geoLat: Double, geoAlt: Double?=0.0, temperature: Double, pressure: Double): Azimuth

  fun getAzimuthFromEcliptic(eclipticPosition: IPos, gmtJulDay: Double, geoLng: Double, geoLat: Double, geoAlt: Double): Azimuth {
    return getAzimuthFromEcliptic(eclipticPosition, gmtJulDay, geoLng, geoLat, geoAlt, 0.0, 1013.25)
  }

  fun getAzimuthFromEcliptic(eclipticPosition: IPos, gmtJulDay: Double, location: Location, temperature: Double = 0.0, pressure: Double = 1013.25): Azimuth {
    return getAzimuthFromEcliptic(eclipticPosition, gmtJulDay, location.lng, location.lat, location.altitudeMeter, temperature, pressure)
  }

  /** 承上 , ChronoLocalDateTime 版本  */
  fun getAzimuthFromEcliptic(eclipticPosition: IPos, gmt: ChronoLocalDateTime<*>, location: Location, temperature: Double, pressure: Double): Azimuth {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getAzimuthFromEcliptic(eclipticPosition, gmtJulDay, location, temperature, pressure)
  }

  /** 由黃經 , 黃緯 , 求得地平方位角  */
  fun getAzimuthFromEquator(equatorPosition: IPos, gmtJulDay: Double, geoLng: Double, geoLat: Double, geoAlt: Double?=0.0, temperature: Double, pressure: Double): Azimuth

  fun getAzimuthFromEquator(equatorPosition: IPos, gmtJulDay: Double, location: Location, temperature: Double, pressure: Double): Azimuth {
    return getAzimuthFromEquator(equatorPosition, gmtJulDay, location.lng, location.lat, location.altitudeMeter, temperature, pressure)
  }

  /** 承上 , ChronoLocalDateTime 版本  */
  fun getAzimuthFromEquator(equatorPosition: IPos, gmt: ChronoLocalDateTime<*>, location: Location, temperature: Double, pressure: Double): Azimuth {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getAzimuthFromEquator(equatorPosition, gmtJulDay, location, temperature, pressure)
  }


}
