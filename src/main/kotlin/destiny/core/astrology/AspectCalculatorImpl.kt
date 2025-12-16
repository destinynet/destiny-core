/**
 * Created by smallufo on 2019-10-18.
 */
package destiny.core.astrology

import com.google.common.collect.Sets
import destiny.core.astrology.IPointAspectPattern.AspectType.APPLYING
import destiny.core.astrology.IPointAspectPattern.AspectType.SEPARATING
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.Score
import java.io.Serializable
import kotlin.math.abs

class AspectCalculatorImpl(
  val aspectEffectiveImpl: IAspectEffective,
  private val pointPosFuncMap: Map<AstroPoint, IPosition<*>>
) : IAspectCalculator, Serializable {


  override fun getAspectPattern(
    p1: AstroPoint, p2: AstroPoint,
    p1PosMap: Map<AstroPoint, IZodiacDegree>, p2PosMap: Map<AstroPoint, IZodiacDegree>,
    laterForP1: ((AstroPoint) -> IZodiacDegree?)?, laterForP2: ((AstroPoint) -> IZodiacDegree?)?, aspects: Set<Aspect>
  ): IPointAspectPattern? {
    return aspects
      .intersect(aspectEffectiveImpl.applicableAspects)
      .let { asps ->
        if (p1 is FixedStar || p2 is FixedStar) {
          setOf(Aspect.CONJUNCTION)
        } else
          asps
      }
      .asSequence()
      .map { aspect ->
        aspect to aspectEffectiveImpl.getEffectiveErrorAndScore(p1, p1PosMap.getValue(p1).zDeg.toZodiacDegree(),
                                                                p2, p2PosMap.getValue(p2).zDeg.toZodiacDegree(), aspect)
      }.firstOrNull { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
      ?.let { (aspect, maybeErrorAndScore) -> aspect to maybeErrorAndScore!! }
      ?.let { (aspect, errorAndScore) ->
        val error = errorAndScore.first
        val score = errorAndScore.second

        laterForP1?.invoke(p1)?.zDeg?.let { deg1Next ->
          laterForP2?.invoke(p2)?.zDeg?.let { deg2Next ->
            val planetsAngleNext = deg1Next.toZodiacDegree().getAngle(deg2Next)
            val errorNext = abs(planetsAngleNext - aspect.degree)
            val type = if (errorNext <= error) APPLYING else SEPARATING
            PointAspectPattern.of(p1, p2, aspect, type, error, score)
          }
        } ?: PointAspectPattern.of(p1, p2, aspect, null, error, score)
      }
  }


  override fun IHoroscopeModel.getAspectPattern(twoPoints: Set<AstroPoint>, aspects: Set<Aspect>): IPointAspectPattern? {

    val posMap: Map<AstroPoint, IPosWithAzimuth> = this.positionMap

    return twoPoints
      .takeIf { it.size == 2 } // 確保裡面只有兩個 Point
      ?.takeIf { set -> posMap.keys.containsAll(set) }
      ?.takeIf { set -> !set.all { it is Axis } } // 過濾四角點互相形成的交角
      ?.takeIf { set -> !set.all { it is LunarNode } } // 過濾南北交點對沖
      ?.let {
        val (p1, p2) = twoPoints.iterator().let { it.next() to it.next() }


        // 8.64 seconds
        val later = this.gmtJulDay.plus(0.0001)

        val laterForP1: ((AstroPoint) -> IZodiacDegree?) = { p -> pointPosFuncMap[p]?.getPosition(later, location, starTypeOptions = StarTypeOptions.MEAN) }
        val laterForP2: ((AstroPoint) -> IZodiacDegree?) = { p -> pointPosFuncMap[p]?.getPosition(later, location, starTypeOptions = StarTypeOptions.MEAN) }

        getAspectPattern(p1, p2, posMap, posMap, laterForP1, laterForP2, aspects)
      }
  }

  /** 針對整體 */
  override fun IHoroscopeModel.getAspectPatterns(points: Set<AstroPoint>, aspects: Set<Aspect>): Set<IPointAspectPattern> {
    return Sets.combinations(points.toSet(), 2)
      .asSequence()
      .mapNotNull { this.getAspectPattern(it, aspects) }
      .toSet()
  }


  override fun getPointAspectAndScore(
    point: AstroPoint,
    positionMap: Map<AstroPoint, IPos>,
    points: Set<AstroPoint>,
    aspects: Set<Aspect>
  ): Set<Triple<AstroPoint, Aspect, Score>> {
    return positionMap[point]?.lngDeg?.let { starDeg ->
      points
        .asSequence()
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
}


