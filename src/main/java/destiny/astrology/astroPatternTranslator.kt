/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.astrology

import destiny.core.Descriptive
import destiny.core.IPattern
import destiny.core.IPatternDescriptor
import destiny.tools.AbstractPropertyBasedPatternDescriptor
import mu.KotlinLogging

/**
 * maybe merged with [destiny.astrology.classical.rules.AbstractPlanetPatternDescriptor]
 */
class AstroPatternDescriptor(pattern: IPattern,
                             commentKey: String,
                             parameters: List<Any>) : AbstractPropertyBasedPatternDescriptor(pattern, commentKey, parameters) {

  override val resource = "destiny.astrology.AstroPatterns"

}


object astroPatternTranslator : IPatternDescriptor<AstroPattern> {

  val logger = KotlinLogging.logger { }

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
      is AstroPattern.Yod -> {
        val bottoms = pattern.bottoms.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms))
        }.invoke()
      }
      is AstroPattern.Boomerang -> {
        val yod = listOf(pattern.yod.pointer.point).plus(pattern.yod.bottoms).joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(yod, pattern.yod.pointer.point, pattern.yod.pointer.sign, pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign, pattern.oppoPoint.house, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(yod, pattern.yod.pointer.point, pattern.yod.pointer.sign, pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign, pattern.oppoPoint.house))
        }.invoke()
      }
      is AstroPattern.GoldenYod -> {
        val bottoms = pattern.bottoms.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms))
        }.invoke()
      }
      is AstroPattern.GrandCross -> {
        val points = pattern.points.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(points, pattern.quality, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(points, pattern.quality))
        }.invoke()
      }
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
