/**
 * @author smallufo
 * Created on 2007/7/24 at 下午 1:34:43
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.tools.serializers.astrology.AstroPointSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

/**
 *                                                         AstroPoint
 *                                                             |
 *                                        +--------------------+--------------------+
 *                                        |                                         |
 *                                      [Star]                                    [Axis]
 *                                        |
 *     +-----------+---------+------------+-----------------+--------------+---------------+
 *     |           |         |            |                 |              |               |
 * [Planet]  [Asteroid]  [FixedStar]  [LunarPoint] (A)  [Hamburger]     [Lot]     [LunarStation]
 *    行星     小行星         恆星        日月交點            漢堡虛星        阿拉伯點         二十八宿
 *                                        |
 *                                        |
 *                               +--------+--------+
 *                               |                 |
 *                             [LunarNode]     LunarApsis
 *                           [TRUE/MEAN]       [MEAN/OSCU]
 *                           North/South   PERIGEE (近)/APOGEE (遠)
 */
@Serializable(with = AstroPointSerializer::class)
abstract class AstroPoint(nameKey: String, resource: String, abbrKey: String?, val unicode: Char? = null) : Point(nameKey, resource, abbrKey) {
  /** 分類（[PointKind]）：呈現層以它分層與套樣式，不要用類別名或 nameKey 的字串比對 */
  val kind: PointKind
    get() = when (this) {
      is Planet    -> PointKind.PLANET
      is LunarNode -> PointKind.NODE
      is Asteroid  -> PointKind.ASTEROID
      is FixedStar -> PointKind.FIXED_STAR
      is Lot    -> PointKind.LOT
      is Axis      -> PointKind.AXIS
      else         -> PointKind.OTHER
    }

  companion object : IPoints<AstroPoint> {

    override val type: KClass<out Point> = AstroPoint::class

    override val values: Array<AstroPoint> by lazy {
      arrayOf(*Star.values, *Axis.values)
    }

    override fun fromString(value: String, locale: Locale): AstroPoint? {
      return Star.fromString(value, locale) ?: Axis.fromString(value, locale)
    }

  }
}

/** [AstroPoint] 的分類；JSON 以名稱輸出（PLANET、NODE、ASTEROID、FIXED_STAR、LOT、AXIS、OTHER），TS 端的 `PointKind` 是它的鏡像 */
@Serializable
enum class PointKind { PLANET, NODE, ASTEROID, FIXED_STAR, LOT, AXIS, OTHER }
