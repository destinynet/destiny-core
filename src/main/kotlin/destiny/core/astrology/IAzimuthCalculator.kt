/**
 * @author smallufo
 * Created on 2007/5/21 at 上午 5:46:16
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import java.io.Serializable

/**
 * 計算地平方位角 , 與 Point/Star/Planet/...等星體種類皆無關，只要座標即可
 * 內定實作是 AzimuthImpl
 */
interface IAzimuthCalculator : Serializable {

  fun IPos.getAzimuth(coordinate: Coordinate,
                      gmtJulDay: GmtJulDay,
                      loc: ILocation,
                      temperature: Double = 0.0,
                      pressure: Double = 1013.25): Azimuth {
    return when(coordinate) {
      Coordinate.ECLIPTIC -> getAzimuthFromEcliptic(this, gmtJulDay, loc, temperature, pressure)
      Coordinate.EQUATORIAL -> getAzimuthFromEquator(this, gmtJulDay, loc, temperature, pressure)
      /** TODO : 恆星座標系統 [Coordinate.SIDEREAL] 計算 [Azimuth] */
      Coordinate.SIDEREAL -> throw RuntimeException("Not Supported")
    }
  }

  /** [Coordinate.ECLIPTIC] 由黃經 , 黃緯 , 求得地平方位角  */
  fun getAzimuthFromEcliptic(eclipticPosition: IPos,
                             gmtJulDay: GmtJulDay,
                             loc: ILocation,
                             temperature: Double = 0.0,
                             pressure: Double = 1013.25): Azimuth


  /** [Coordinate.EQUATORIAL] 由赤經 , 赤緯 , 求得地平方位角  */
  fun getAzimuthFromEquator(equatorPosition: IPos,
                            gmtJulDay: GmtJulDay,
                            loc: ILocation,
                            temperature: Double = 0.0,
                            pressure: Double = 1013.25): Azimuth

}
