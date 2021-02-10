/**
 * Created by kevin.huang on 2019-10-18.
 */
package destiny.core.astrology

import com.google.common.collect.Sets
import destiny.core.astrology.AspectData.Type.APPLYING
import destiny.core.astrology.AspectData.Type.SEPARATING
import java.io.Serializable
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class AspectsCalculatorImpl(
  val aspectEffectiveImpl: IAspectEffective,
  private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
  private val houseCuspImpl: IHouseCusp,
  private val pointPosFuncMap: Map<Point, IPosition<*>>
) : IAspectsCalculator, Serializable {


  private fun IHoroscopeModel.getAspectData(twoPoints: Set<Point>, aspects: Collection<Aspect>): AspectData? {

    val posMap = this.positionMap

    return twoPoints
      .takeIf { it.size == 2 } // 確保裡面只有兩個 Point
      ?.takeIf { set -> posMap.keys.containsAll(set) }
      ?.takeIf { set -> !set.all { it is Axis } } // 過濾四角點互相形成的交角
      ?.takeIf { set -> !set.all { it is LunarNode } } // 過濾南北交點對沖
      ?.let {
        val (p1, p2) = twoPoints.iterator().let { it.next() to it.next() }

        aspects
          .intersect(aspectEffectiveImpl.applicableAspects)
          .asSequence()
          .map { aspect ->
            aspect to aspectEffectiveImpl.getEffectiveErrorAndScore(p1, posMap.getValue(p1).lng, p2, posMap.getValue(p2).lng, aspect)
          }.firstOrNull { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
          ?.let { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!! }
          ?.let { (aspect, errorAndScore) ->
            val error = errorAndScore.first
            val score = errorAndScore.second

            val lmt = this.lmt //目前時間
            val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

            val hContext: IHoroscopeContext =
              HoroscopeContext(starPosWithAzimuth, houseCuspImpl, pointPosFuncMap, this.points, this.houseSystem,
                this.coordinate, this.centric)
            val h2 = hContext.getHoroscope(lmt = later, loc = this.location, place = this.place, points = this.points)

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
  override fun IHoroscopeModel.getAspectData(points: Collection<Point>, aspects: Collection<Aspect>): Set<AspectData> {
    return Sets.combinations(points.toSet(), 2)
      .asSequence()
      .mapNotNull { this.getAspectData(it, aspects) }
      .toSet()
  }

  /** 針對單一 */
  override fun getAspectData(point: Point,
                             h: IHoroscopeModel,
                             points: Collection<Point>,
                             aspects: Collection<Aspect>): Set<AspectData> {
    return points
      .asSequence()
      .map { eachPoint -> setOf(point, eachPoint) }
      .mapNotNull { twoPoints -> h.getAspectData(twoPoints, aspects) }
      .toSet()
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
          aspects
            .intersect(aspectEffectiveImpl.applicableAspects)
            .map { eachAspect ->
              eachAspect to aspectEffectiveImpl.getEffectiveErrorAndScore(point, starDeg, eachPoint, eachDeg, eachAspect)
            }
            .filter { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
            .map { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!!.second }
            .map { (aspect, score) ->
              Triple(eachPoint, aspect, score)
            }
        }.toSet()
    } ?: emptySet()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AspectsCalculatorImpl) return false

    if (aspectEffectiveImpl != other.aspectEffectiveImpl) return false
    if (starPosWithAzimuth != other.starPosWithAzimuth) return false
    if (houseCuspImpl != other.houseCuspImpl) return false
    if (pointPosFuncMap != other.pointPosFuncMap) return false

    return true
  }

  override fun hashCode(): Int {
    var result = aspectEffectiveImpl.hashCode()
    result = 31 * result + starPosWithAzimuth.hashCode()
    result = 31 * result + houseCuspImpl.hashCode()
    result = 31 * result + pointPosFuncMap.hashCode()
    return result
  }
}


