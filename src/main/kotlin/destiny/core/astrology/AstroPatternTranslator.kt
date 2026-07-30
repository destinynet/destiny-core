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

  /**
   * [PointSignHouse.house] 為 null（無精確出生時刻，宮位不可得）時，改用不含宮位的訊息模板
   * （key 後綴 [NO_HOUSE]），並由 [params] 把 null 的宮位從參數列中剔除，
   * 使 NoHouse 模板的 `{n}` 索引自然往前遞補。
   *
   * 為何不是「渲染完再用 regex 把宮位字樣洗掉」：模板是多語系的（zh / en / ja），
   * 括號形式與語序各不相同，事後掃散文既脆弱又只顧得到英文。
   * 由 null 選 key，正確性改由 properties 檔本身保證。
   *
   * [AstroPattern.Boomerang] 有兩個宮位，任一為 null 就整組不渲染 —— 它們同源於一張盤，
   * 不會出現一有一無。
   */
  private const val NO_HOUSE = ".NoHouse"

  private fun key(base: String, vararg houses: Int?) = if (houses.all { it != null }) base else base + NO_HOUSE

  private fun params(vararg items: Any?): List<Any> = items.filterNotNull()

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
        val h = pattern.squared.house
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, key("commentScore", h),
            params(
              *pattern.oppoPoints.toTypedArray(), pattern.squared.sign, h, pattern.squared.point,
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, key("commentBasic", h),
          params(*pattern.oppoPoints.toTypedArray(), pattern.squared.sign, h, pattern.squared.point)
        )
      }
      is AstroPattern.Yod -> {
        val h = pattern.apex.house
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, key("commentScore", h),
            params(
              pattern.apex.point, pattern.apex.sign, h, *pattern.bottoms.toTypedArray(),
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, key("commentBasic", h),
          params(pattern.apex.point, pattern.apex.sign, h, *pattern.bottoms.toTypedArray())
        )
      }
      is AstroPattern.Boomerang -> {
        val yod = listOf(pattern.yod.apex.point).plus(pattern.yod.bottoms).toTypedArray()

        val h1 = pattern.yod.apex.house
        val h2 = pattern.oppoPoint.house
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, key("commentScore", h1, h2),
            params(
              *yod, pattern.yod.apex.point, pattern.yod.apex.sign,
              h1, pattern.oppoPoint.point, pattern.oppoPoint.sign,
              h2, score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, key("commentBasic", h1, h2),
          params(
            *yod, pattern.yod.apex.point, pattern.yod.apex.sign,
            h1, pattern.oppoPoint.point, pattern.oppoPoint.sign,
            h2
          )
        )
      }
      is AstroPattern.GoldenYod -> {
        val h = pattern.pointer.house
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, key("commentScore", h),
            params(
              pattern.pointer.point,
              pattern.pointer.sign,
              h,
              *pattern.bottoms.toTypedArray(),
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, key("commentBasic", h),
          params(pattern.pointer.point, pattern.pointer.sign, h, *pattern.bottoms.toTypedArray())
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
        val h = pattern.mediator.house
        pattern.score?.let { score ->
          AstroPatternDescriptor(
            pattern, key("commentScore", h),
            params(
              *pattern.oppoPoints.toTypedArray(),
              pattern.mediator.point,
              pattern.mediator.sign,
              h,
              score
            )
          )
        } ?: AstroPatternDescriptor(
          pattern, key("commentBasic", h),
          params(
            *pattern.oppoPoints.toTypedArray(),
            pattern.mediator.point,
            pattern.mediator.sign,
            h
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
