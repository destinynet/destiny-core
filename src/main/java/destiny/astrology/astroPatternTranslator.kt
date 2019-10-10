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

        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*pattern.points.toTypedArray(), pattern.element, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(*pattern.points.toTypedArray(), pattern.element))
        }.invoke()
      }
      is AstroPattern.Kite -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.head.point, *pattern.wings.toTypedArray(), pattern.tail.point, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.head.point, *pattern.wings.toTypedArray(), pattern.tail.point))
        }.invoke()
      }
      is AstroPattern.TSquared -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(*pattern.oppoPoints.toTypedArray(), pattern.squared.sign, pattern.squared.house, pattern.squared.point,
                                        score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(*pattern.oppoPoints.toTypedArray(), pattern.squared.sign, pattern.squared.house, pattern.squared.point))
        }.invoke()
      }
      is AstroPattern.Yod -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray(),
                                        score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray()))
        }.invoke()
      }
      is AstroPattern.Boomerang -> {
        val yod = listOf(pattern.yod.pointer.point).plus(pattern.yod.bottoms).toTypedArray()

        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
                                 listOf(*yod, pattern.yod.pointer.point, pattern.yod.pointer.sign,
                                        pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign,
                                        pattern.oppoPoint.house, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
                                 listOf(*yod, pattern.yod.pointer.point, pattern.yod.pointer.sign,
                                        pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign,
                                        pattern.oppoPoint.house))
        }.invoke()
      }
      is AstroPattern.GoldenYod -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore",
            listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray(), score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic",
            listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray()))
        }.invoke()
      }
      is AstroPattern.GrandCross -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*pattern.points.toTypedArray(), pattern.quality, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(*pattern.points.toTypedArray(), pattern.quality))
        }.invoke()
      }
      is AstroPattern.DoubleT -> {
        val (group1, group2) = pattern.tSquares.iterator().let { iterator ->
          val group1 = iterator.next().let { t -> listOf(t.squared.point).plus(t.oppoPoints) }.toTypedArray()
          val group2 = iterator.next().let { t -> listOf(t.squared.point).plus(t.oppoPoints) }.toTypedArray()
          group1 to group2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*group1, *group2, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(*group1, *group2))
        }.invoke()
      }
      is AstroPattern.Hexagon -> {
        val (group1, group2) = pattern.grandTrines.iterator().let { iterator ->
          val group1 = iterator.next().points.toTypedArray()
          val group2 = iterator.next().points.toTypedArray()
          group1 to group2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*group1, *group2, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(*group1, *group2))
        }.invoke()
      }
      is AstroPattern.Wedge -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" ,
                                 listOf(*pattern.oppoPoints.toTypedArray() , pattern.moderator.point , pattern.moderator.sign , pattern.moderator.house , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" ,
                                 listOf(*pattern.oppoPoints.toTypedArray() , pattern.moderator.point , pattern.moderator.sign , pattern.moderator.house ))
        }.invoke()
      }
      is AstroPattern.MysticRectangle -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" , listOf(*pattern.points.toTypedArray() , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" , listOf(*pattern.points.toTypedArray()))
        }.invoke()
      }
      is AstroPattern.Pentagram -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern , "commentScore" , listOf(*pattern.points.toTypedArray() , score))
        }?: {
          AstroPatternDescriptor(pattern , "commentBasic" , listOf(*pattern.points.toTypedArray()))
        }.invoke()
      }
      is AstroPattern.StelliumSign -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.points, pattern.sign, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.points, pattern.sign))
        }.invoke()
      }
      is AstroPattern.StelliumHouse -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.points, pattern.house, score))
        } ?: {
          AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.points, pattern.house))
        }.invoke()
      }
      is AstroPattern.Confrontation -> {
        val (group1, group2) = pattern.clusters.iterator().let { iterator ->
          val g1 = iterator.next()
          val g2 = iterator.next()
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
