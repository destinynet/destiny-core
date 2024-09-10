/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.core.astrology

import destiny.core.Descriptive
import destiny.core.IPattern
import destiny.core.IPatternDescriptor
import destiny.tools.AbstractPropertyBasedPatternDescriptor
import destiny.tools.KotlinLogging

/**
 * maybe merged with [destiny.core.astrology.classical.rules.AbstractPlanetPatternDescriptor]
 */
class AstroPatternDescriptor(
  pattern: IPattern,
  commentKey: String,
  parameters: List<Any>
) :
  AbstractPropertyBasedPatternDescriptor(pattern, commentKey, parameters) {

  override val resource = AstroPattern::class.qualifiedName!!

}


object AstroPatternTranslator : IPatternDescriptor<AstroPattern> {

  val logger = KotlinLogging.logger { }

  override fun getDescriptor(pattern: AstroPattern): Descriptive {
    return when (pattern) {
      is AstroPattern.GrandTrine -> {

        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern,
            "commentScore",
            listOf(*pattern.points.toTypedArray(), pattern.element, score)
          )
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(*pattern.points.toTypedArray(), pattern.element))
      }
      is AstroPattern.Kite -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern,
            "commentScore",
            listOf(pattern.head.point, *pattern.wings.toTypedArray(), pattern.tail.point, score)
          )
        } ?: AstroPatternDescriptor(
          pattern,
          "commentBasic",
          listOf(pattern.head.point, *pattern.wings.toTypedArray(), pattern.tail.point)
        )
      }
      is AstroPattern.TSquared -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, "commentScore",
            listOf(
              *pattern.oppoPoints.toTypedArray(), pattern.squared.sign, pattern.squared.house, pattern.squared.point,
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, "commentBasic",
          listOf(*pattern.oppoPoints.toTypedArray(), pattern.squared.sign, pattern.squared.house, pattern.squared.point)
        )
      }
      is AstroPattern.Yod -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, "commentScore",
            listOf(
              pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray(),
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, "commentBasic",
          listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray())
        )
      }
      is AstroPattern.Boomerang -> {
        val yod = listOf(pattern.yod.pointer.point).plus(pattern.yod.bottoms).toTypedArray()

        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, "commentScore",
            listOf(
              *yod, pattern.yod.pointer.point, pattern.yod.pointer.sign,
              pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign,
              pattern.oppoPoint.house, score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, "commentBasic",
          listOf(
            *yod, pattern.yod.pointer.point, pattern.yod.pointer.sign,
            pattern.yod.pointer.house, pattern.oppoPoint.point, pattern.oppoPoint.sign,
            pattern.oppoPoint.house
          )
        )
      }
      is AstroPattern.GoldenYod -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, "commentScore",
            listOf(
              pattern.pointer.point,
              pattern.pointer.sign,
              pattern.pointer.house,
              *pattern.bottoms.toTypedArray(),
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, "commentBasic",
          listOf(pattern.pointer.point, pattern.pointer.sign, pattern.pointer.house, *pattern.bottoms.toTypedArray())
        )
      }
      is AstroPattern.GrandCross -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern,
            "commentScore",
            listOf(*pattern.points.toTypedArray(), pattern.quality, score)
          )
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(*pattern.points.toTypedArray(), pattern.quality))
      }
      is AstroPattern.DoubleT -> {
        val (group1, group2) = pattern.tSquares.iterator().let { iterator ->
          val group1 = iterator.next().let { t -> listOf(t.squared.point).plus(t.oppoPoints) }.toTypedArray()
          val group2 = iterator.next().let { t -> listOf(t.squared.point).plus(t.oppoPoints) }.toTypedArray()
          group1 to group2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*group1, *group2, score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(*group1, *group2))
      }
      is AstroPattern.Hexagon -> {
        val (group1, group2) = pattern.grandTrines.iterator().let { iterator ->
          val group1 = iterator.next().points.toTypedArray()
          val group2 = iterator.next().points.toTypedArray()
          group1 to group2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*group1, *group2, score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(*group1, *group2))
      }
      is AstroPattern.Wedge -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, "commentScore",
            listOf(
              *pattern.oppoPoints.toTypedArray(),
              pattern.moderator.point,
              pattern.moderator.sign,
              pattern.moderator.house,
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, "commentBasic",
          listOf(
            *pattern.oppoPoints.toTypedArray(),
            pattern.moderator.point,
            pattern.moderator.sign,
            pattern.moderator.house
          )
        )
      }
      is AstroPattern.MysticRectangle -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*pattern.points.toTypedArray(), score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(*pattern.points.toTypedArray()))
      }
      is AstroPattern.Pentagram -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(*pattern.points.toTypedArray(), score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(*pattern.points.toTypedArray()))
      }
      is AstroPattern.StelliumSign -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.points, pattern.sign, score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.points, pattern.sign))
      }
      is AstroPattern.StelliumHouse -> {
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(pattern.points, pattern.house, score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(pattern.points, pattern.house))
      }
      is AstroPattern.Confrontation -> {
        val (group1, group2) = pattern.clusters.iterator().let { iterator ->
          val g1 = iterator.next()
          val g2 = iterator.next()
          g1 to g2
        }
        pattern.score?.let { score ->
          AstroPatternDescriptor(pattern, "commentScore", listOf(group1, group2, score))
        } ?: AstroPatternDescriptor(pattern, "commentBasic", listOf(group1, group2))
      }
    }
  }

}
