/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:28:37
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 取得星體的位置。支援 Planet , Asteroid , Hamburger <br></br>
 * Swiss Ephemeris 的實作是 StarPositionImpl <br></br>
 * 原廠還支援 月亮的 Apsis , 但我使用 ApsisIF 來實作
 */
interface IStarPosition<out T : IStarPos> {

  /** 設定觀測地點，對於 [Centric.TOPO] 有用 . 2017-07-03 註記 : 此 method 無法移除 */
  fun setLocation(location: ILocation)

  fun getPosition(star: Star, gmtJulDay: Double, centric: Centric, coordinate: Coordinate): T

  /** 同樣是求 Position , 但多傳入地點、溫度、壓力 等資料 , 在此直接 discard 掉  */
  fun getPosition(star: Star,
                  gmtJulDay: Double,
                  geoLng: Double,
                  geoLat: Double,
                  geoAlt: Double? = 0.0,
                  centric: Centric,
                  coordinate: Coordinate,
                  temperature: Double,
                  pressure: Double): T {
    return getPosition(star, gmtJulDay, centric, coordinate)
  }

  fun getPosition(star: Star,
                  gmtJulDay: Double,
                  geoLng: Double,
                  geoLat: Double,
                  geoAlt: Double,
                  centric: Centric,
                  coordinate: Coordinate): T {
    return getPosition(star, gmtJulDay, geoLng, geoLat, geoAlt, centric, coordinate, 0.0, 1013.25)
  }

  fun getPosition(star: Star, gmtJulDay: Double, loc: ILocation, centric: Centric, coordinate: Coordinate): T {
    return getPosition(star, gmtJulDay, loc.lng, loc.lat, loc.altitudeMeter, centric, coordinate, 0.0,
                       1013.25)
  }

  /**
   * @param gmt GMT 的 Gregorian 時刻
   */
  fun getPosition(star: Star, gmt: ChronoLocalDateTime<*>, centric: Centric, coordinate: Coordinate): T {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getPosition(star, gmtJulDay, centric, coordinate)
  }


  /**
   * 取得星體的位置 , 已 GMT 時間計算 , 包含 溫度、壓力
   */
  fun getPosition(star: Star,
                  gmt: ChronoLocalDateTime<*>,
                  centric: Centric,
                  coordinate: Coordinate,
                  loc: ILocation,
                  temperature: Double,
                  pressure: Double): IPos {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getPosition(star, gmtJulDay, loc.lng, loc.lat, loc.altitudeMeter, centric, coordinate,
                       temperature, pressure)
  }


  /** 取得星體的位置 , 包含當地時間 (LMT) 以及座標 , 包含 溫度、壓力 */
  fun getPosition(star: Star,
                  lmt: ChronoLocalDateTime<*>,
                  location: ILocation,
                  centric: Centric,
                  coordinate: Coordinate,
                  temperature: Double = 0.0,
                  pressure: Double = 1013.25): IPos {
    val gmt = TimeTools.getGmtFromLmt(lmt, location.timeZone.toZoneId())
    return getPosition(star, gmt, centric, coordinate, location, temperature, pressure)
  }

}
/** 取得星體的位置 , 包含當地時間 (LMT) 以及座標 , 「不包含」 溫度、壓力  */
