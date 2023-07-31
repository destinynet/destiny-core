/**
 * Created by smallufo at 2008/11/11 下午 5:21:34
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import java.io.Serializable

/** 星體順逆資訊 */
data class StarRetrograde(val gmtJulDay: GmtJulDay, val star: Star, val type: StationaryType, val pos: IStarPos) : Serializable

/**
 * 計算星體在黃道帶上 逆行 / Stationary (停滯) 的介面，目前 SwissEph 的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！)
 * SwissEph 內定實作是 RetrogradeImpl
 */
interface IRetrograde {

  /**
   * 下次停滯的時間為何時 (GMT)
   */
  fun getNextStationary(star: Star, fromGmt: GmtJulDay, forward: Boolean): GmtJulDay

  /**
   * 承上，不僅計算下次（或上次）的停滯時間
   * 另外計算，該次停滯，是準備「順轉逆」，或是「逆轉順」
   */
  fun getNextStationary(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): StarRetrograde {
    val nextStationary: GmtJulDay = getNextStationary(star, fromGmt, forward)
    val pos = starPositionImpl.getPosition(star, nextStationary, Centric.GEO, Coordinate.ECLIPTIC)
    // 分別取 滯留前、後 來比對
    val prior = nextStationary - 1 / 1440.0
    val after = nextStationary + 1 / 1440.0
    val pos1 = starPositionImpl.getPosition(star, prior, Centric.GEO, Coordinate.ECLIPTIC)
    val pos2 = starPositionImpl.getPosition(star, after, Centric.GEO, Coordinate.ECLIPTIC)

    val type = if (pos1.speedLng > 0 && pos2.speedLng < 0)
      StationaryType.DIRECT_TO_RETROGRADE
    else if (pos1.speedLng < 0 && pos2.speedLng > 0)
      StationaryType.RETROGRADE_TO_DIRECT
    else
      throw RuntimeException("Error , 滯留前 speed = " + pos1.speedLng + " , 滯留後 speed = " + pos2.speedLng)

    return StarRetrograde(nextStationary, star, type, pos)
  }


  /**
   * 列出一段時間內，某星體的順逆過程
   */
  fun getPeriodStationary(star: Star, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>): List<StarRetrograde> {
    require(fromGmt < toGmt) {
      "toGmt ($toGmt) should >= fromGmt($fromGmt)"
    }

    return generateSequence(getNextStationary(star, fromGmt, true, starPositionImpl)) {
      getNextStationary(star, it.gmtJulDay + (1 / 1440.0), true, starPositionImpl)
    }.takeWhile { it.gmtJulDay <= toGmt }
      .toList()
  }


  /**
   * 取得一段時間內，這些星體的順逆過程，按照時間排序
   */
  fun getStarsPeriodStationary(stars: Set<Star>, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>): List<StarRetrograde> {
    require(fromGmt < toGmt) {
      "toGmt ($toGmt) should >= fromGmt($fromGmt)"
    }

    return stars.flatMap { star ->
      generateSequence(getNextStationary(star, fromGmt, true, starPositionImpl)) {
        getNextStationary(star, it.gmtJulDay + (1 / 1440.0), true, starPositionImpl)
      }.takeWhile { it.gmtJulDay <= toGmt }
    }.sortedBy { it.gmtJulDay }
      .toList()
  }

}
