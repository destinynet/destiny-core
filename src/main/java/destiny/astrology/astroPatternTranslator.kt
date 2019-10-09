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
                             parameters: List<Any>) :
  AbstractPropertyBasedPatternDescriptor(pattern, commentKey, parameters) {

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
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(oppo, pattern.squared.sign, pattern.squared.house, pattern.squared.point,
                                        score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(oppo, pattern.squared.sign, pattern.squared.house, pattern.squared.point))
        }.invoke()
      }
      is AstroPattern.Yod -> {
        val bottoms = pattern.bottoms.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms,
                                        score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms))
        }.invoke()
      }
      is AstroPattern.Boomerang -> {
        val yod = listOf(pattern.yod.pointer.point).plus(pattern.yod.bottoms).joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(yod, pattern.yod.pointer.point, pattern.yod.pointer.sign,
                                        pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign,
                                        pattern.oppoPoint.house, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(yod, pattern.yod.pointer.point, pattern.yod.pointer.sign,
                                        pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign,
                                        pattern.oppoPoint.house))
        }.invoke()
      }
      is AstroPattern.GoldenYod -> {
        val bottoms = pattern.bottoms.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms,
                                        score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, bottoms))
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
      is AstroPattern.DoubleT -> {
        val (group1, group2) = pattern.tSquares.iterator().let { iterator ->
          val group1 = iterator.next().let { t -> listOf(t.squared.point).plus(t.oppoPoints) }.joinToString(",")
          val group2 = iterator.next().let { t -> listOf(t.squared.point).plus(t.oppoPoints) }.joinToString(",")
          group1 to group2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(group1, group2, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(group1, group2))
        }.invoke()
      }
      is AstroPattern.Hexagon -> {
        val (group1, group2) = pattern.grandTrines.iterator().let { iterator ->
          val group1 = iterator.next().points.joinToString(",")
          val group2 = iterator.next().points.joinToString(",")
          group1 to group2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(group1, group2, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(group1, group2))
        }.invoke()
      }
      is AstroPattern.Wedge -> {
        val oppo = pattern.oppoPoints.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" ,
                                 listOf(oppo , pattern.moderator.point , pattern.moderator.sign , pattern.moderator.house , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" ,
                                 listOf(oppo , pattern.moderator.point , pattern.moderator.sign , pattern.moderator.house ))
        }.invoke()
      }
      is AstroPattern.MysticRectangle -> {
        val points = pattern.points.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" , listOf(points , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" , listOf(points))
        }.invoke()
      }
      is AstroPattern.Pentagram -> {
        val points = pattern.points.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" , listOf(points , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" , listOf(points))
        }.invoke()
      }
      is AstroPattern.StelliumSign -> {
        val points = pattern.points.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(points, pattern.sign, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(points, pattern.sign))
        }.invoke()
      }
      is AstroPattern.StelliumHouse -> {
        val points = pattern.points.joinToString(",")
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(points, pattern.house, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(points, pattern.house))
        }.invoke()
      }
      is AstroPattern.Confrontation -> {
        val (group1, group2) = pattern.clusters.iterator().let { iterator ->
          val g1 = iterator.next().joinToString(",")
          val g2 = iterator.next().joinToString(",")
          g1 to g2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" , listOf(group1 , group2 , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" , listOf(group1 , group2))
        }.invoke()
      }
    }
  }

}
