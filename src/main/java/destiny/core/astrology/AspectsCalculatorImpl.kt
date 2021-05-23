/**
 * Created by smallufo on 2019-10-18.
 */
package destiny.core.astrology

import com.google.common.collect.Sets
import destiny.core.astrology.IAspectData.Type.APPLYING
import destiny.core.astrology.IAspectData.Type.SEPARATING
import java.io.Serializable
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class AspectsCalculatorImpl(val aspectEffectiveImpl: IAspectEffective,
                            private val pointPosFuncMap: Map<Point, IPosition<*>>) : IAspectsCalculator, Serializable {


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
            aspect to aspectEffectiveImpl.getEffectiveErrorAndScore(p1, posMap.getValue(p1).lngDeg, p2, posMap.getValue(p2).lngDeg, aspect)
          }.firstOrNull { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
          ?.let { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!! }
          ?.let { (aspect, errorAndScore) ->
            val error = errorAndScore.first
            val score = errorAndScore.second

            val lmt = this.lmt //目前時間
            val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

            val deg1Next = pointPosFuncMap[p1]!!.getPosition(later, location).lngDeg
            val deg2Next = pointPosFuncMap[p2]!!.getPosition(later, location).lngDeg
            val planetsAngleNext = deg1Next.getAngle(deg2Next)
            val errorNext = abs(planetsAngleNext - aspect.degree)

            val type = if (errorNext <= error) APPLYING else SEPARATING
            AspectData(p1, p2, aspect, error, score, type , this.gmtJulDay)
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
    return positionMap[point]?.lngDeg?.let { starDeg ->
      points
        .filter { it !== point }
        .filter { positionMap.containsKey(it) }
        .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
        .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
        .flatMap { eachPoint ->
          val eachDeg = positionMap.getValue(eachPoint).lngDeg
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
    if (pointPosFuncMap != other.pointPosFuncMap) return false

    return true
  }

  override fun hashCode(): Int {
    var result = aspectEffectiveImpl.hashCode()
    result = 31 * result + pointPosFuncMap.hashCode()
    return result
  }
}


