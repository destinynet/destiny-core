/**
 * Created by smallufo on 2019-05-12.
 */
package destiny.astrology

import destiny.core.IPattern
import java.io.Serializable

sealed class AstroPattern(override val name: String,
                          override val notes: String? = null) : IPattern, Serializable {

  override fun toString(): String {
    return "AstroPattern(notes=$notes)"
  }

  // T-Squared
  class 三刑會沖(private val oppoPoints: Set<Point>, private val squaredPoint: Point) : AstroPattern("三刑會沖", "$oppoPoints 正沖，兩者均與 $squaredPoint 相刑")


}

interface IPatternFactory {

  fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern>

}

object AstroPatterns {

  val classicalList = Planet.classicalList

  val squareCalculator = modernCalculator {
    aspects = setOf(Aspect.SQUARE)
  }

  // 只計算 0,60,90,120,180 五個重要交角
  private val importantAspectsCalculator = modernCalculator {
    aspects = Aspect.getAngles(Aspect.Importance.HIGH)
  }

  val importantCalculator = HoroscopeAspectsCalculator(importantAspectsCalculator)


  object TSquared : IPatternFactory, Serializable {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {

      val aspectDataSet = importantCalculator.getAspectDataSet(starPosMap, classicalList)

      return aspectDataSet
        .filter { it.aspect == Aspect.OPPOSITION }
        .map { it.twoPoints }
        .map { twoPoints: Set<Point> ->
          val (p1, p2) = twoPoints.toList().let { it[0] to it[1] }
          val p1SquaredPoints = squareCalculator.getPointAspect(p1, starPosMap).keys
          val p2SquaredPoints = squareCalculator.getPointAspect(p2, starPosMap).keys
          twoPoints to p1SquaredPoints.intersect(p2SquaredPoints)
        }.filter { (_, commonSquaredPoints) ->
          commonSquaredPoints.isNotEmpty()
        }.flatMap { (two, squared) ->
          squared.map { squareP ->
            AstroPattern.三刑會沖(two, squareP)
          }
        }.toSet()

    }
  }
}

