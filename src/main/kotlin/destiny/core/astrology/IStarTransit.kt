/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:14:07
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay

/**
 * 計算某星 Transit 的介面
 * 某星下次（或上次）行進到黃道/恆星 帶上某一點的時間 , 赤道座標系不支援!
 * SwissEph 內定實作是 StarTransitImpl
 *
 * 計算星體 Transit 到黃道某點的時間，僅限於 [Planet] , [Asteroid] , Moon's [LunarNode]
 */
interface IStarTransit {

  fun getNextTransitGmt(star: Star, degrees: Set<ZodiacDegree>, fromGmt: GmtJulDay, forward: Boolean = true, coordinate: Coordinate = Coordinate.ECLIPTIC): Pair<ZodiacDegree, GmtJulDay>

  /**
   * 傳回 GMT Julian Day 時刻
   */
  fun getNextTransitGmt(star: Star, degree: ZodiacDegree, fromGmt: GmtJulDay, forward: Boolean = true, coordinate: Coordinate = Coordinate.ECLIPTIC): GmtJulDay {
    return getNextTransitGmt(star, setOf(degree), fromGmt, forward, coordinate).second
  }

  /**
   * 某段時間之內， 此 [star] 與 行經這些 黃道度數 [degrees] 的時刻
   */
  fun getRangeTransitGmt(star: Star, degrees: Set<ZodiacDegree>, fromGmt: GmtJulDay, toGmtJulDay: GmtJulDay, forward: Boolean = true, coordinate: Coordinate = Coordinate.ECLIPTIC): Sequence<Pair<ZodiacDegree, GmtJulDay>> {
    return generateSequence(getNextTransitGmt(star, degrees, fromGmt, forward, coordinate)) { (_ , gmt) ->
      getNextTransitGmt(star, degrees, gmt+ 0.001, forward, coordinate)
    }.takeWhile { (_,gmt) -> gmt < toGmtJulDay }
  }

}
