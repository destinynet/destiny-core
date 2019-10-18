/**
 * Created by kevin.huang on 2019-10-18.
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

class HoroscopeAspectsCalculatorImpl(
  private val aspectEffectiveImpl: IAspectEffective,
  private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
  private val houseCuspImpl: IHouseCusp,
  private val pointPosFuncMap: Map<Point, IPosition<*>>
                                    ) : IHoroscopeAspectsCalculator, Serializable {


  private val aspectDataFun = { twoPoints: Set<Point>, aspects: Collection<Aspect>, h: IHoroscopeModel ->

    val posMap = h.positionMap

    twoPoints
      .takeIf { it.size == 2 } // 確保裡面只有兩個 Point
      ?.takeIf { set -> posMap.keys.containsAll(set) }
      ?.takeIf { set -> !set.all { it is Axis } } // 過濾四角點互相形成的交角
      ?.takeIf { set -> !set.all { it is LunarNode } } // 過濾南北交點對沖
      ?.let {
        val (p1, p2) = twoPoints.iterator().let { it.next() to it.next() }

        aspects.map { aspect ->
          aspect to aspectEffectiveImpl.getEffectiveErrorAndScore(p1, posMap.getValue(p1).lng, p2,
                                                                  posMap.getValue(p2).lng, aspect)
        }.firstOrNull { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
          ?.let { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!! }
          ?.let { (aspect, errorAndScore) ->
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
            AspectData(p1, p2, aspect, error, score, type)
          }
      }

  }

  /** 針對整體 */
  override fun getAspectData(h: IHoroscopeModel,
                             points: Collection<Point>,
                             aspects: Collection<Aspect>): Set<AspectData> {
    //val posMap: Map<Point, IPosWithAzimuth> = h.positionMap
    return Sets.combinations(points.toSet(), 2)
      .asSequence()
      .filter { set -> !set.all { it is Axis } } // 過濾四角點互相形成的交角
      .filter { set -> !set.all { it is LunarNode } } // 過濾南北交點對沖
      .mapNotNull { aspectDataFun.invoke(it, aspects, h) }
      .toSet()
  }

  /** 針對單一 */
  override fun getAspectData(point: Point,
                             h: IHoroscopeModel,
                             points: Collection<Point>,
                             aspects: Collection<Aspect>): Set<AspectData> {

    val positionMap = h.positionMap



    return positionMap[point]?.lng?.let { starDeg ->
      points
        .asSequence()
        .filter { it !== point }
        .filter { positionMap.containsKey(it) }
        .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
        .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
        .mapNotNull { aspectDataFun.invoke(setOf(point, it), aspects, h) }
        .toSet()
    } ?: emptySet()
  }

  override fun getPointAspectAndScore(point: Point,
                                      positionMap: Map<Point, IPos>,
                                      points: Collection<Point>,
                                      aspects: Collection<Aspect>): Set<Triple<Point, Aspect, Double>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }


  override fun getTitle(locale: Locale): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getDescription(locale: Locale): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }

}