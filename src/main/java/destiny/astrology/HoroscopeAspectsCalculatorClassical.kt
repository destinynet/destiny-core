/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 2:19:45
 */
package destiny.astrology

import com.google.common.collect.Sets
import destiny.astrology.Aspect.Importance
import destiny.astrology.AspectData.AspectType.APPLYING
import destiny.astrology.AspectData.AspectType.SEPARATING
import destiny.astrology.classical.AspectEffectiveClassical
import destiny.astrology.classical.IPointDiameter
import java.io.Serializable
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs







/**
 * 古典占星術，列出一張星盤中呈現交角的星體以及角度 的實作
 *  */
class HoroscopeAspectsCalculatorClassical(
  val classical: AspectEffectiveClassical,
  private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
  private val houseCuspImpl : IHouseCusp,
  private val pointPosFuncMap: Map<Point, IPosition<*>>) : IHoroscopeAspectsCalculator, Serializable {

  val planetOrbsImpl: IPointDiameter = classical.planetOrbsImpl

  override fun getAspectData(h: IHoroscopeModel, points: Collection<Point>, aspects: Collection<Aspect>): Set<AspectData> {

    val posMap = h.positionMap
    return Sets.combinations(points.toSet() , 2)
      .asSequence()
      .filter { set -> !set.all { it is Axis } } // 過濾四角點互相形成的交角
      .filter { set -> !set.all { it is LunarNode } } // 過濾南北交點對沖
      .flatMap { set ->
        val (p1 , p2) = set.iterator().let { it.next() to it.next() }

        aspects
          .asSequence()
          .filter { Aspect.getAngles(Importance.HIGH).contains(it) } // 只比對 0 , 60 , 90 , 120 , 180 五個度數
          .map { aspect ->
            aspect to classical.getEffectiveErrorAndScore(p1, posMap.getValue(p1).lng, p2, posMap.getValue(p2).lng, aspect)
          }.filter { (_ , errorAndScore) -> errorAndScore != null }
          .map { (aspect , errorAndScore) -> aspect to errorAndScore!! }
          .map { (aspect, errorAndScore) ->
            val error = errorAndScore.first
            val score = errorAndScore.second

            val lmt = h.lmt //目前時間
            val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

            val hContext : IHoroscopeContext = HoroscopeContext(starPosWithAzimuth, houseCuspImpl, pointPosFuncMap, h.points, h.houseSystem, h.coordinate, h.centric)
            val h2 = hContext.getHoroscope(lmt = later, loc = h.location, place = h.place, points = h.points)

            val deg1Next = h2.getPositionWithAzimuth(p1).lng
            val deg2Next = h2.getPositionWithAzimuth(p2).lng
            val planetsAngleNext = IHoroscopeModel.getAngle(deg1Next, deg2Next)
            val errorNext = abs(planetsAngleNext - aspect.degree)

            val type = if (errorNext <= error) APPLYING else SEPARATING
            AspectData(p1 , p2 , aspect , error , score , type)
          }
      }.toSet()
  }

  override fun getAspectData(point: Point,
                             h: IHoroscopeModel,
                             points: Collection<Point>,
                             aspects: Collection<Aspect>): Set<AspectData> {

    return point.takeIf { it is Planet } // 只計算行星
      ?.let {
        h.positionMap[point]?.lng?.let { planetDeg ->

          points.filter { it !== point }
            .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
            .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
            .flatMap { eachPoint ->
              val eachPlanetDeg = h.positionMap.getValue(eachPoint).lng
              aspects
                .asSequence()
                .filter { Aspect.getAngles(Importance.HIGH).contains(it) } // 只比對 0 , 60 , 90 , 120 , 180 五個度數
                .map { aspect -> aspect to classical.getEffectiveErrorAndScore(point, planetDeg, eachPoint, eachPlanetDeg, aspect) }
                .filter { (_ , maybeErrorAndScore) -> maybeErrorAndScore!= null }
                .map { (aspect , maybeErrorAndScore) -> aspect to maybeErrorAndScore!! }
                .map { (aspect, errorAndScore) ->

                  val error = errorAndScore.first

                  val lmt = h.lmt //目前時間
                  val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

                  val hContext : IHoroscopeContext = HoroscopeContext(starPosWithAzimuth, houseCuspImpl, pointPosFuncMap, h.points, h.houseSystem, h.coordinate, h.centric)
                  val h2 = hContext.getHoroscope(lmt = later, loc = h.location, place = h.place, points = h.points)

                  val deg1Next = h2.getPositionWithAzimuth(point).lng
                  val deg2Next = h2.getPositionWithAzimuth(eachPoint).lng
                  val planetsAngleNext = IHoroscopeModel.getAngle(deg1Next, deg2Next)
                  val errorNext = abs(planetsAngleNext - aspect.degree)

                  val type = if (errorNext <= error) APPLYING else SEPARATING

                  AspectData(point , eachPoint , aspect , error , errorAndScore.second , type)
                }
                .toList()
            }.toSet()
        }?: emptySet()
      }?: emptySet()
  }

  override fun getPointAspectAndScore(point: Point,
                                      positionMap: Map<Point, IPos>,
                                      points: Collection<Point>,
                                      aspects: Collection<Aspect>): Set<Triple<Point, Aspect, Double>> {

    return point.takeIf { it is Planet } // 只計算行星
      ?.let {
        positionMap[point]?.lng?.let { planetDeg ->

          points.filter { it !== point }
            .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
            .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
            .flatMap { eachPoint ->
              val eachPlanetDeg = positionMap.getValue(eachPoint).lng
              aspects
                .asSequence()
                .filter { Aspect.getAngles(Importance.HIGH).contains(it) } // 只比對 0 , 60 , 90 , 120 , 180 五個度數
                .map { aspect -> aspect to classical.getEffectiveErrorAndScore(point, planetDeg, eachPoint, eachPlanetDeg, aspect) }
                .filter { (_ , maybeErrorAndScore) -> maybeErrorAndScore!= null }
                .map { (aspect , maybeErrorAndScore) -> aspect to maybeErrorAndScore!!.second }
                .map { (aspect , score) ->
                  Triple(eachPoint , aspect , score)
                }
                .toList()
            }.toSet()
        }?: emptySet()
      }?: emptySet()
  }

  override fun getTitle(locale: Locale): String {
    return "古典占星術 : " + classical.planetOrbsImpl.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return "古典占星術實作 : " + classical.planetOrbsImpl.getDescription(locale)
  }


}
