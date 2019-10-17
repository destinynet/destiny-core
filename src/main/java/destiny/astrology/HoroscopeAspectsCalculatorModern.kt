/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:55:27
 */
package destiny.astrology

import com.google.common.collect.Sets
import destiny.astrology.AspectData.Type.APPLYING
import destiny.astrology.AspectData.Type.SEPARATING
import mu.KotlinLogging
import java.io.Serializable
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

/**
 * 現代占星術，計算一張星盤中，星體交角列表的實作
 * */

class HoroscopeAspectsCalculatorModern(private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
                                       private val houseCuspImpl: IHouseCusp,
                                       private val pointPosFuncMap: Map<Point, IPosition<*>>) :
  IHoroscopeAspectsCalculator, Serializable {

  private val modern: AspectEffectiveModern = AspectEffectiveModern()

  override fun getAspectData(h: IHoroscopeModel,
                             points: Collection<Point>,
                             aspects: Collection<Aspect>): Set<AspectData> {
    val posMap: Map<Point, IPosWithAzimuth> = h.positionMap

    return Sets.combinations(points.toSet(), 2)
      .asSequence()
      .filter { set -> !set.all { it is Axis } } // 過濾四角點互相形成的交角
      .filter { set -> !set.all { it is LunarNode } } // 過濾南北交點對沖
      .flatMap { set ->
        val (p1, p2) = set.iterator().let { it.next() to it.next() }

        aspects.asSequence().map { aspect ->
          aspect to modern.getEffectiveErrorAndScore(p1, posMap.getValue(p1).lng, p2, posMap.getValue(p2).lng, aspect)
        }.filter { (_, errorAndScore) -> errorAndScore != null }
          .map { (aspect, errorAndScore) -> aspect to errorAndScore!! }
          .map { (aspect, errorAndScore) ->
            val error = errorAndScore.first
            val score = errorAndScore.second

            val lmt = h.lmt //目前時間
            val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

            val hContext: IHoroscopeContext =
              HoroscopeContext(starPosWithAzimuth, houseCuspImpl, pointPosFuncMap, h.points, h.houseSystem,
                h.coordinate, h.centric)
            val h2 = hContext.getHoroscope(lmt = later, loc = h.location, place = h.place, points = h.points)

            val deg1Next = h2.getPositionWithAzimuth(p1).lng
            val deg2Next = h2.getPositionWithAzimuth(p2).lng
            val planetsAngleNext = IHoroscopeModel.getAngle(deg1Next, deg2Next)
            val errorNext = abs(planetsAngleNext - aspect.degree)


            val type = if (errorNext <= error) APPLYING else SEPARATING

            logger.debug("[{} , {}] type = {} , error = {} , errorNext = {}", p1, p2, type, error, errorNext)

            AspectData(p1, p2, aspect, error, score, type)
          }
      }.toSet()
  }

  override fun getAspectData(point: Point,
                             h: IHoroscopeModel,
                             points: Collection<Point>,
                             aspects: Collection<Aspect>): Set<AspectData> {

    val positionMap = h.positionMap
    return positionMap[point]?.lng?.let { starDeg ->
      points
        .filter { it !== point }
        .filter { positionMap.containsKey(it) }
        .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
        .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
        .flatMap { eachPoint ->
          val eachDeg = positionMap.getValue(eachPoint).lng
          aspects.map { eachAspect ->
            eachAspect to modern.getEffectiveErrorAndScore(point, starDeg, eachPoint, eachDeg, eachAspect)
          }.filter { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
            .map { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!! }
            .map { (aspect, errorAndScore) ->
              val error = errorAndScore.first

              logger.trace("{} 與 {} 目前 error = {}", point, eachPoint, error)

              val lmt = h.lmt //目前時間
              val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

              val hContext: IHoroscopeContext =
                HoroscopeContext(starPosWithAzimuth, houseCuspImpl, pointPosFuncMap, h.points, h.houseSystem,
                  h.coordinate, h.centric)
              val h2 = hContext.getHoroscope(lmt = later, loc = h.location, place = h.place, points = h.points)

              val deg1Next = h2.getPositionWithAzimuth(point).lng
              val deg2Next = h2.getPositionWithAzimuth(eachPoint).lng
              val planetsAngleNext = IHoroscopeModel.getAngle(deg1Next, deg2Next)
              val errorNext = abs(planetsAngleNext - aspect.degree)

              logger.trace("{} 與 {} 稍後 error = {}", point, eachPoint, errorNext)

              val type = if (errorNext <= error) APPLYING else SEPARATING

              AspectData(point, eachPoint, aspect, error, errorAndScore.second, type)
            }
        }.toSet()
    } ?: emptySet()
  }

  override fun getPointAspectAndScore(point: Point,
                                      positionMap: Map<Point, IPos>,
                                      points: Collection<Point>,
                                      aspects: Collection<Aspect>): Set<Triple<Point, Aspect, Double>> {
    return positionMap[point]?.lng?.let { starDeg ->
      points
        .filter { it !== point }
        .filter { positionMap.containsKey(it) }
        .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
        .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
        .flatMap { eachPoint ->
          val eachDeg = positionMap.getValue(eachPoint).lng
          aspects.map { eachAspect ->
            eachAspect to modern.getEffectiveErrorAndScore(point, starDeg, eachPoint, eachDeg, eachAspect)
          }
            .filter { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
            .map { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!!.second }
            .map { (aspect, score) ->
              Triple(eachPoint, aspect, score)
            }
        }.toSet()
    } ?: emptySet()
  }

  override fun getTitle(locale: Locale): String {
    return "現代占星術"
  }

  override fun getDescription(locale: Locale): String {
    return "現代占星術實作"
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }

}
