/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.astrology

import destiny.core.Descriptive
import destiny.core.IPattern
import destiny.core.IPatternDescriptor
import destiny.tools.AbstractPropertyBasedPatternDescriptor

/**
 * maybe merged with [destiny.astrology.classical.rules.AbstractPlanetPatternDescriptor]
 */
class AstroPatternDescriptor(pattern: IPattern,
                             commentKey: String,
                             parameters: List<Any>) : AbstractPropertyBasedPatternDescriptor(pattern, commentKey, parameters) {

  override val resource = "destiny.astrology.AstroPatterns"

}


object astroPatternTranslator : IPatternDescriptor<AstroPattern> {

  override fun getDescriptor(pattern: AstroPattern): Descriptive {
    return when (pattern) {
      is AstroPattern.GrandTrine -> {
        val stars = pattern.points.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(stars, pattern.element, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(stars, pattern.element))
        }.invoke()
      }
      is AstroPattern.Kite -> {
        val wings = pattern.wings.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.head.point, wings, pattern.tail.point, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.head.point, wings, pattern.tail.point))
        }.invoke()
      }
      is AstroPattern.TSquared -> {
        val oppo = pattern.oppoPoints.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(oppo, pattern.squared.sign, pattern.squared.house, pattern.squared.point, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(oppo, pattern.squared.sign, pattern.squared.house, pattern.squared.point))
        }.invoke()
      }
      is AstroPattern.Yod -> TODO()
      is AstroPattern.Boomerang -> TODO()
      is AstroPattern.GoldenYod -> TODO()
      is AstroPattern.GrandCross -> TODO()
      is AstroPattern.DoubleT -> TODO()
      is AstroPattern.Hexagon -> TODO()
      is AstroPattern.Wedge -> TODO()
      is AstroPattern.MysticRectangle -> TODO()
      is AstroPattern.Pentagram -> TODO()
      is AstroPattern.StelliumSign -> TODO()
      is AstroPattern.StelliumHouse -> TODO()
      is AstroPattern.Confrontation -> TODO()
    }
  }

}
