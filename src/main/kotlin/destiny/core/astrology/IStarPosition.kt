/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:28:37
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 取得星體的位置。支援 [Planet] , [Asteroid] , [Hamburger]
 * Swiss Ephemeris 的實作是 [StarPositionImpl]
 * 原廠還支援 月亮的 Apsis , 但我使用 [IApsis] 來實作
 */
interface IStarPosition<out T : IStarPos> {

  /** 設定觀測地點，對於 [Centric.TOPO] 有用 . 2017-07-03 註記 : 此 method 無法移除 */
  fun setLocation(location: ILocation)

  fun getPosition(star: Star, gmtJulDay: GmtJulDay, centric: Centric, coordinate: Coordinate): T

  fun getPositions(stars: Iterable<Star>, gmtJulDay: GmtJulDay, centric: Centric, coordinate: Coordinate): Map<Star, T>

  /** 同樣是求 Position , 但多傳入地點、溫度、壓力 等資料 , 在此直接 discard 掉  */
  fun getPosition(star: Star,
                  gmtJulDay: GmtJulDay,
                  loc: ILocation,
                  centric: Centric = Centric.GEO,
                  coordinate: Coordinate = Coordinate.ECLIPTIC,
                  temperature: Double = 0.0,
                  pressure: Double = 1013.25): T {
    return getPosition(star, gmtJulDay, centric, coordinate)
  }


  fun getPosition(star: Star, gmtJulDay: GmtJulDay, loc: ILocation, centric: Centric = Centric.GEO, coordinate: Coordinate = Coordinate.ECLIPTIC): T {
    return getPosition(star, gmtJulDay, loc, centric, coordinate, 0.0, 1013.25)
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
    return getPosition(star, gmtJulDay, loc, centric, coordinate, temperature, pressure)
  }


  /** 取得星體的位置 , 包含當地時間 (LMT) 以及座標 , 包含 溫度、壓力 */
  fun getPosition(star: Star,
                  lmt: ChronoLocalDateTime<*>,
                  location: ILocation,
                  centric: Centric,
                  coordinate: Coordinate,
                  temperature: Double = 0.0,
                  pressure: Double = 1013.25): IPos {
    val gmt = TimeTools.getGmtFromLmt(lmt, location.zoneId)
    return getPosition(star, gmt, centric, coordinate, location, temperature, pressure)
  }

}
/** 取得星體的位置 , 包含當地時間 (LMT) 以及座標 , 「不包含」 溫度、壓力  */
