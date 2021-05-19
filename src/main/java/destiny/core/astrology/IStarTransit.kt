/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:14:07
 */
package destiny.core.astrology

import destiny.core.calendar.JulDayResolver
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算某星 Transit 的介面
 * 某星下次（或上次）行進到黃道/恆星 帶上某一點的時間 , 赤道座標系不支援!
 * SwissEph 內定實作是 StarTransitImpl
 *
 * 計算星體 Transit 到黃道某點的時間，僅限於 [Planet] , [Asteroid] , Moon's [LunarNode]
 */
interface IStarTransit {

  /**
   * 傳回 GMT Julian Day 時刻
   */
  fun getNextTransitGmt(star: Star, degree: ZodiacDegree, coordinate: Coordinate, fromGmt: Double, forward: Boolean = true): Double


  /**
   * 傳回 GMT [ChronoLocalDateTime]
   */
  fun getNextTransitGmtDateTime(star: Star,
                                degree: ZodiacDegree,
                                coordinate: Coordinate,
                                fromGmt: Double,
                                forward: Boolean = true,
                                julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
    val gmtJulDay = getNextTransitGmt(star, degree, coordinate, fromGmt, forward)
    return julDayResolver.getLocalDateTime(gmtJulDay)
  }

}
