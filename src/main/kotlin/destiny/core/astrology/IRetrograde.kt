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
import destiny.core.calendar.IEvent

enum class RetrogradePhase {
  PREPARING,
  RETROGRADING,
  LEAVING
}

/** 星體順逆三相之一 , Span  */
data class RetrogradeSpan(
  val phase: RetrogradePhase,
  override val star: Star,
  override val begin: GmtJulDay,
  override val end: GmtJulDay,
  override val fromPos: IPos,
  override val toPos: IPos
) : IStarEventSpan

/** 星體停滯資訊 */
data class Stationary(val gmtJulDay: GmtJulDay, val star: Star, val type: StationaryType, val pos: IPos) : IEvent {
  override val begin: GmtJulDay
    get() = gmtJulDay
}


data class RetrogradeCycle(
  val star: Star,
  val preparingGmt: GmtJulDay, val preparingPos: IPos,
  val retrogradingGmt: GmtJulDay, val retrogradingPos: IPos,
  val returningGmt: GmtJulDay, val returningPos: IPos,
  val leavingGmt: GmtJulDay, val leavingPos: IPos
) {

  /** 星體順逆三相 Map */
  val phaseMap: Map<RetrogradePhase, RetrogradeSpan> by lazy {
    mapOf(
      PREPARING to RetrogradeSpan(PREPARING, star, preparingGmt, retrogradingGmt, preparingPos, retrogradingPos),
      RETROGRADING to RetrogradeSpan(RETROGRADING, star, retrogradingGmt, returningGmt, retrogradingPos, returningPos),
      LEAVING to RetrogradeSpan(LEAVING, star, returningGmt, leavingGmt, returningPos, leavingPos)
    )
  }

  val stationaries: List<Stationary> by lazy {
    listOf(
      Stationary(retrogradingGmt, star, DIRECT_TO_RETROGRADE, retrogradingPos),
      Stationary(returningGmt, star, RETROGRADE_TO_DIRECT, retrogradingPos)
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
   * [star] : 支援 [Planet] 以及 月亮的 [LunarNode.NORTH_TRUE] / [LunarNode.SOUTH_TRUE]
   */
  fun getNextStationary(star: Star, fromGmt: GmtJulDay, forward: Boolean): GmtJulDay

  /**
   * 下次停滯的時間為何時 (GMT) , 黃道座標為何
   */
  private fun getNextStationaryPos(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): Pair<GmtJulDay, IPos> {
    return getNextStationary(star, fromGmt, forward).let { gmtJulDay ->
      gmtJulDay to starPositionImpl.getPosition(star, gmtJulDay, GEO, ECLIPTIC)
    }
  }

  private fun getNextStationaryPosType(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): Triple<GmtJulDay, IPos, StationaryType> {
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
  fun getNextStationary(star: Star, fromGmt: GmtJulDay, forward: Boolean, starPositionImpl: IStarPosition<*>): Stationary {
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

    return Stationary(nextStationary, star, type, pos)
  }

  /**
   * 取得星體逆行三態 , 支援順推以及逆推
   */
  fun getNextStationaryCycle(star: Star, fromGmt: GmtJulDay, forward: Boolean = true, starPositionImpl: IStarPosition<*>, transit: IStarTransit): RetrogradeCycle {
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

    return RetrogradeCycle(star, preparingGmt, preparingPos, retrogradingGmt, retrogradingPos, returningGmt, returningPos, leavingGmt, leavingPos)
  }

  /**
   * 取得某範圍內，此星體的順逆三態
   */
  fun getNextStationaryCycles(star: Star, fromGmt: GmtJulDay, toGmtJulDay: GmtJulDay, starPositionImpl: IStarPosition<*>, transit: IStarTransit): List<RetrogradeCycle> {
    return generateSequence(getNextStationaryCycle(star, fromGmt, true, starPositionImpl, transit)) {
      val next = it.leavingGmt + 1
      getNextStationaryCycle(star, next, true, starPositionImpl, transit)
    }.takeWhile { it.preparingGmt < toGmtJulDay }
      .toList()
  }





  /**
   * 列出一段時間內，某星體的順逆過程
   */
  fun getPeriodStationary(star: Star, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>): List<Stationary> {
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
  fun getPeriodStationaries(stars: Set<Star>, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>): List<Stationary> {
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


  fun getPeriodCycles(stars: Set<Star>, fromGmt: GmtJulDay, toGmt: GmtJulDay, starPositionImpl: IStarPosition<*>, transit: IStarTransit): List<RetrogradeCycle> {
    require(fromGmt < toGmt) {
      "toGmt ($toGmt) should >= fromGmt($fromGmt)"
    }

    return stars.filter { it != Planet.MOON }.flatMap { star ->
      generateSequence(getNextStationaryCycle(star, fromGmt, true, starPositionImpl, transit)) {
        getNextStationaryCycle(star, it.leavingGmt + 1, true, starPositionImpl, transit)
      }.takeWhile {
        it.preparingGmt in fromGmt..toGmt || it.leavingGmt in fromGmt..toGmt
          || it.preparingGmt < fromGmt && toGmt < it.leavingGmt
      }
    }.sortedBy { it.retrogradingGmt }
      .toList()
  }

  /**
   * 取得一段時間內，這些星體的逆行過程，按照時間排序
   * [phases] : 搜尋哪些 [RetrogradePhase]
   */
  fun getPeriodRetrogrades(stars: Set<Star>, fromGmt: GmtJulDay, toGmt: GmtJulDay, phases : Set<RetrogradePhase> = setOf(RETROGRADING), starPositionImpl: IStarPosition<*>, transit: IStarTransit): List<RetrogradeSpan> {
    require(fromGmt < toGmt) {
      "toGmt ($toGmt) should >= fromGmt($fromGmt)"
    }

    return getPeriodCycles(stars, fromGmt, toGmt, starPositionImpl, transit)
      .flatMap { it.phaseMap.toList() }
      .filter { (phase, _) -> phases.contains(phase) }
      .filter { (_, span) ->
        span.begin in fromGmt..toGmt || span.end in fromGmt .. toGmt
          || span.begin < fromGmt && toGmt < span.end
      }
      .map { pair -> pair.second }
      .toList()
  }

}
