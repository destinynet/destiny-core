/**
 * Created by smallufo at 2008/11/11 下午 5:21:34
 */
package destiny.core.astrology

import destiny.core.astrology.Centric.GEO
import destiny.core.astrology.Coordinate.ECLIPTIC
import destiny.core.astrology.RetrogradePhase.*
import destiny.core.astrology.StationaryType.DIRECT_TO_RETROGRADE
import destiny.core.astrology.StationaryType.RETROGRADE_TO_DIRECT
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.toInstant
import java.io.Serializable
import kotlin.time.Duration

enum class RetrogradePhase {
  PREPARING,
  RETROGRADING,
  LEAVING
}

data class RetrogradeSpan(val fromGmt: GmtJulDay, val toGmtJulDay: GmtJulDay, val fromPos: IPos, val toPos: IPos) {
  val duration: Duration by lazy {
    toGmtJulDay.toInstant().minus(fromGmt.toInstant())
  }
}

data class StarRetrogradeCycle(
  val star: Star,
  val preparingGmt: GmtJulDay, val preparingPos: IPos,
  val retrogradingGmt: GmtJulDay, val retrogradingPos: IPos,
  val returningGmt: GmtJulDay, val returningPos: IPos,
  val leavingGmt: GmtJulDay, val leavingPos: IPos
) {

  val phaseMap: Map<RetrogradePhase, RetrogradeSpan> by lazy {
    mapOf(
      PREPARING to RetrogradeSpan(preparingGmt, retrogradingGmt, preparingPos, retrogradingPos),
      RETROGRADING to RetrogradeSpan(retrogradingGmt, returningGmt, retrogradingPos, returningPos),
      LEAVING to RetrogradeSpan(returningGmt, leavingGmt, returningPos, leavingPos)
    )
  }
}

/** 星體順逆資訊 */
data class StarRetrograde(val gmtJulDay: GmtJulDay, val star: Star, val type: StationaryType, val pos: IPos) : Serializable

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
   * 下次停滯的時間為何時 (GMT) , 黃道座標為何
   */
  fun getNextStationaryPos(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): Pair<GmtJulDay, IPos> {
    return getNextStationary(star, fromGmt, forward).let { gmtJulDay ->
      gmtJulDay to starPositionImpl.getPosition(star, gmtJulDay, GEO, ECLIPTIC)
    }
  }

  fun getNextStationaryPosType(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): Triple<GmtJulDay, IPos, StationaryType> {
    val (nextStationary, nextPos) = getNextStationaryPos(star, fromGmt, forward, starPositionImpl)

    val prior = nextStationary - 1 / 1440.0
    val after = nextStationary + 1 / 1440.0
    val pos1 = starPositionImpl.getPosition(star, prior, GEO, ECLIPTIC)
    val pos2 = starPositionImpl.getPosition(star, after, GEO, ECLIPTIC)

    val type: StationaryType = if (pos1.speedLng > 0 && pos2.speedLng < 0)
      DIRECT_TO_RETROGRADE
    else if (pos1.speedLng < 0 && pos2.speedLng > 0)
      RETROGRADE_TO_DIRECT
    else
      throw RuntimeException("Error , 滯留前 speed = " + pos1.speedLng + " , 滯留後 speed = " + pos2.speedLng)

    return Triple(nextStationary, nextPos, type)
  }

  /**
   * 取得星體逆行三態 , 目前只支援順行推導
   */
  fun getNextStationaryCycle(star: Star, fromGmt: GmtJulDay, forward: Boolean = true, starPositionImpl: IStarPosition<*>, transit: IStarTransit): StarRetrogradeCycle {
    val (nextStationary, nextPos, type) = getNextStationaryPosType(star, fromGmt, forward, starPositionImpl)

    val retrogradingGmt: GmtJulDay
    val retrogradingPos: IPos

    val returningGmt: GmtJulDay
    val returningPos: IPos

    when (type) {
      DIRECT_TO_RETROGRADE -> {

        if (forward) {
          // 順推
          val (prevReturningGmt, prevReturningPos) = getNextStationaryPos(star, fromGmt, false, starPositionImpl)
          val (prevRetroGmt, prevRetroPos) = getNextStationaryPos(star, prevReturningGmt - 1, false, starPositionImpl)
          val leftGmt = transit.getNextTransitGmt(star, prevRetroPos.lngDeg, prevReturningGmt, true, ECLIPTIC)
          if (leftGmt > fromGmt) {
            // 處理 returning 到 leaving 的範圍
            retrogradingGmt = prevRetroGmt
            retrogradingPos = prevRetroPos

            returningGmt = prevReturningGmt
            returningPos = prevReturningPos
          } else {
            getNextStationaryPos(star, nextStationary + 1, true, starPositionImpl).also {
              returningGmt = it.first
              returningPos = it.second
            }

            retrogradingGmt = nextStationary
            retrogradingPos = nextPos
          }
        } else {
          // 逆推
          getNextStationaryPos(star, nextStationary + 1, true, starPositionImpl).also {
            returningGmt = it.first
            returningPos = it.second
          }
          retrogradingGmt = nextStationary
          retrogradingPos = nextPos
        }
      }

      RETROGRADE_TO_DIRECT -> {
        if (forward) {
          // 順推
          getNextStationaryPos(star, fromGmt, false, starPositionImpl).also {
            retrogradingGmt = it.first
            retrogradingPos = it.second
          }
          returningGmt = nextStationary
          returningPos = nextPos
        } else {
          // 逆推
          val (nextRetrogradeGmt, nextRetrogradePos) = getNextStationaryPos(star, fromGmt, true, starPositionImpl)
          val (nextReturnGmt, nextReturnPos) = getNextStationaryPos(star, nextRetrogradeGmt + 1, true, starPositionImpl)
          val prepareGmt = transit.getNextTransitGmt(star, nextReturnPos.lngDeg, nextRetrogradeGmt, false, ECLIPTIC)
          if (fromGmt > prepareGmt) {
            // 處理 preparing 到 retrograde 的範圍
            retrogradingGmt = nextRetrogradeGmt
            retrogradingPos = nextRetrogradePos

            returningGmt = nextReturnGmt
            returningPos = nextReturnPos
          } else {
            getNextStationaryPos(star, nextStationary - 1, false, starPositionImpl).also {
              retrogradingGmt = it.first
              retrogradingPos = it.second
            }
            returningGmt = nextStationary
            returningPos = nextPos
          }
        }
      }
    }

    val preparingGmt = transit.getNextTransitGmt(star, returningPos.lngDeg, retrogradingGmt, false, ECLIPTIC)
    val preparingPos = starPositionImpl.getPosition(star, preparingGmt, GEO, ECLIPTIC)

    val leavingGmt = transit.getNextTransitGmt(star, retrogradingPos.lngDeg, returningGmt, true, ECLIPTIC)
    val leavingPos = starPositionImpl.getPosition(star, leavingGmt, GEO, ECLIPTIC)

    return StarRetrogradeCycle(star, preparingGmt, preparingPos, retrogradingGmt, retrogradingPos, returningGmt, returningPos, leavingGmt, leavingPos)
  }

  /**
   * 取得某範圍內，此星體的順逆三態
   */
  fun getNextStationaryCycles(star: Star, fromGmt: GmtJulDay, toGmtJulDay: GmtJulDay, starPositionImpl: IStarPosition<*>, transit: IStarTransit): List<StarRetrogradeCycle> {
    return generateSequence(getNextStationaryCycle(star, fromGmt, true, starPositionImpl, transit)) {
      val next = it.leavingGmt + 1
      getNextStationaryCycle(star, next, true, starPositionImpl, transit)
    }.takeWhile { it.preparingGmt < toGmtJulDay }
      .toList()
  }


  /**
   * 承上，不僅計算下次（或上次）的停滯時間
   * 另外計算，該次停滯，是準備「順轉逆」，或是「逆轉順」
   */
  fun getNextStationary(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): StarRetrograde {
    val nextStationary: GmtJulDay = getNextStationary(star, fromGmt, forward)
    val pos = starPositionImpl.getPosition(star, nextStationary, GEO, ECLIPTIC)
    // 分別取 滯留前、後 來比對
    val prior = nextStationary - 1 / 1440.0
    val after = nextStationary + 1 / 1440.0
    val pos1 = starPositionImpl.getPosition(star, prior, GEO, ECLIPTIC)
    val pos2 = starPositionImpl.getPosition(star, after, GEO, ECLIPTIC)

    val type = if (pos1.speedLng > 0 && pos2.speedLng < 0)
      DIRECT_TO_RETROGRADE
    else if (pos1.speedLng < 0 && pos2.speedLng > 0)
      RETROGRADE_TO_DIRECT
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
