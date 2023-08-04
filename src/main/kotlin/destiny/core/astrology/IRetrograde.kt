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
import java.io.Serializable

enum class RetrogradePhase {
  PREPARING,
  RETROGRADING,
  LEAVING
}

/** 星體順逆三相之一 , Span  */
data class RetrogradeSpan(override val fromGmt: GmtJulDay,
                          override val toGmt: GmtJulDay,
                          override val fromPos: IPos,
                          override val toPos: IPos) : IEvent

/** 星體順逆資訊 */
data class PlanetRetrograde(val gmtJulDay: GmtJulDay, val planet: Planet, val type: StationaryType, val pos: IPos) : Serializable

data class PlanetRetrogradeCycle(
  val planet: Planet,
  val preparingGmt: GmtJulDay, val preparingPos: IPos,
  val retrogradingGmt: GmtJulDay, val retrogradingPos: IPos,
  val returningGmt: GmtJulDay, val returningPos: IPos,
  val leavingGmt: GmtJulDay, val leavingPos: IPos
) {

  /** 星體順逆三相 Map */
  val phaseMap: Map<RetrogradePhase, RetrogradeSpan> by lazy {
    mapOf(
      PREPARING to RetrogradeSpan(preparingGmt, retrogradingGmt, preparingPos, retrogradingPos),
      RETROGRADING to RetrogradeSpan(retrogradingGmt, returningGmt, retrogradingPos, returningPos),
      LEAVING to RetrogradeSpan(returningGmt, leavingGmt, returningPos, leavingPos)
    )
  }

  val stationaries: List<PlanetRetrograde> by lazy {
    listOf(
      PlanetRetrograde(retrogradingGmt, planet, DIRECT_TO_RETROGRADE, retrogradingPos),
      PlanetRetrograde(returningGmt, planet, RETROGRADE_TO_DIRECT, retrogradingPos)
    )
  }
}



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
   * 承上，不僅計算下次（或上次）的停滯時間
   * 另外計算，該次停滯，是準備「順轉逆」，或是「逆轉順」
   */
  fun getNextStationary(planet: Planet, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): PlanetRetrograde {
    val nextStationary: GmtJulDay = getNextStationary(planet, fromGmt, forward)
    val pos = starPositionImpl.getPosition(planet, nextStationary, GEO, ECLIPTIC)
    // 分別取 滯留前、後 來比對
    val prior = nextStationary - 1 / 1440.0
    val after = nextStationary + 1 / 1440.0
    val pos1 = starPositionImpl.getPosition(planet, prior, GEO, ECLIPTIC)
    val pos2 = starPositionImpl.getPosition(planet, after, GEO, ECLIPTIC)

    val type = if (pos1.speedLng > 0 && pos2.speedLng < 0)
      DIRECT_TO_RETROGRADE
    else if (pos1.speedLng < 0 && pos2.speedLng > 0)
      RETROGRADE_TO_DIRECT
    else
      throw RuntimeException("Error , 滯留前 speed = " + pos1.speedLng + " , 滯留後 speed = " + pos2.speedLng)

    return PlanetRetrograde(nextStationary, planet, type, pos)
  }

  /**
   * 取得星體逆行三態 , 支援順推以及逆推
   */
  fun getNextStationaryCycle(planet: Planet, fromGmt: GmtJulDay, forward: Boolean = true, starPositionImpl: IStarPosition<*>, transit: IStarTransit): PlanetRetrogradeCycle {
    val (nextStationary, nextPos, type) = getNextStationaryPosType(planet, fromGmt, forward, starPositionImpl)

    val retrogradingGmt: GmtJulDay
    val retrogradingPos: IPos

    val returningGmt: GmtJulDay
    val returningPos: IPos

    when (type) {
      DIRECT_TO_RETROGRADE -> {

        if (forward) {
          // 順推
          val (prevReturningGmt, prevReturningPos) = getNextStationaryPos(planet, fromGmt, false, starPositionImpl)
          val (prevRetroGmt, prevRetroPos) = getNextStationaryPos(planet, prevReturningGmt - 1, false, starPositionImpl)
          val leftGmt = transit.getNextTransitGmt(planet, prevRetroPos.lngDeg, prevReturningGmt, true, ECLIPTIC)
          if (leftGmt > fromGmt) {
            // 處理 returning 到 leaving 的範圍
            retrogradingGmt = prevRetroGmt
            retrogradingPos = prevRetroPos

            returningGmt = prevReturningGmt
            returningPos = prevReturningPos
          } else {
            getNextStationaryPos(planet, nextStationary + 1, true, starPositionImpl).also {
              returningGmt = it.first
              returningPos = it.second
            }

            retrogradingGmt = nextStationary
            retrogradingPos = nextPos
          }
        } else {
          // 逆推
          getNextStationaryPos(planet, nextStationary + 1, true, starPositionImpl).also {
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
          getNextStationaryPos(planet, fromGmt, false, starPositionImpl).also {
            retrogradingGmt = it.first
            retrogradingPos = it.second
          }
          returningGmt = nextStationary
          returningPos = nextPos
        } else {
          // 逆推
          val (nextRetrogradeGmt, nextRetrogradePos) = getNextStationaryPos(planet, fromGmt, true, starPositionImpl)
          val (nextReturnGmt, nextReturnPos) = getNextStationaryPos(planet, nextRetrogradeGmt + 1, true, starPositionImpl)
          val prepareGmt = transit.getNextTransitGmt(planet, nextReturnPos.lngDeg, nextRetrogradeGmt, false, ECLIPTIC)
          if (fromGmt > prepareGmt) {
            // 處理 preparing 到 retrograde 的範圍
            retrogradingGmt = nextRetrogradeGmt
            retrogradingPos = nextRetrogradePos

            returningGmt = nextReturnGmt
            returningPos = nextReturnPos
          } else {
            getNextStationaryPos(planet, nextStationary - 1, false, starPositionImpl).also {
              retrogradingGmt = it.first
              retrogradingPos = it.second
            }
            returningGmt = nextStationary
            returningPos = nextPos
          }
        }
      }
    }

    val preparingGmt = transit.getNextTransitGmt(planet, returningPos.lngDeg, retrogradingGmt, false, ECLIPTIC)
    val preparingPos = starPositionImpl.getPosition(planet, preparingGmt, GEO, ECLIPTIC)

    val leavingGmt = transit.getNextTransitGmt(planet, retrogradingPos.lngDeg, returningGmt, true, ECLIPTIC)
    val leavingPos = starPositionImpl.getPosition(planet, leavingGmt, GEO, ECLIPTIC)

    return PlanetRetrogradeCycle(planet, preparingGmt, preparingPos, retrogradingGmt, retrogradingPos, returningGmt, returningPos, leavingGmt, leavingPos)
  }

  /**
   * 取得某範圍內，此星體的順逆三態
   */
  fun getNextStationaryCycles(planet: Planet, fromGmt: GmtJulDay, toGmtJulDay: GmtJulDay, starPositionImpl: IStarPosition<*>, transit: IStarTransit): List<PlanetRetrogradeCycle> {
    return generateSequence(getNextStationaryCycle(planet, fromGmt, true, starPositionImpl, transit)) {
      val next = it.leavingGmt + 1
      getNextStationaryCycle(planet, next, true, starPositionImpl, transit)
    }.takeWhile { it.preparingGmt < toGmtJulDay }
      .toList()
  }





  /**
   * 列出一段時間內，某星體的順逆過程
   */
  fun getPeriodStationary(planet: Planet, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>): List<PlanetRetrograde> {
    require(fromGmt < toGmt) {
      "toGmt ($toGmt) should >= fromGmt($fromGmt)"
    }

    return generateSequence(getNextStationary(planet, fromGmt, true, starPositionImpl)) {
      getNextStationary(planet, it.gmtJulDay + (1 / 1440.0), true, starPositionImpl)
    }.takeWhile { it.gmtJulDay <= toGmt }
      .toList()
  }


  /**
   * 取得一段時間內，這些星體的順逆過程，按照時間排序
   */
  fun getStarsPeriodStationary(planets: Set<Planet>, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>): List<PlanetRetrograde> {
    require(fromGmt < toGmt) {
      "toGmt ($toGmt) should >= fromGmt($fromGmt)"
    }

    return planets.flatMap { planet ->
      generateSequence(getNextStationary(planet, fromGmt, true, starPositionImpl)) {
        getNextStationary(planet, it.gmtJulDay + (1 / 1440.0), true, starPositionImpl)
      }.takeWhile { it.gmtJulDay <= toGmt }
    }.sortedBy { it.gmtJulDay }
      .toList()
  }

}
