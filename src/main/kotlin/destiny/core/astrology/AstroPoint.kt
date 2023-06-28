/**
 * @author smallufo
 * Created on 2007/7/24 at 下午 1:34:43
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
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
 * [Planet]  [Asteroid]  [FixedStar]  [LunarPoint] (A)  [Hamburger]     [Arabic]     [LunarStation]
 *    行星     小行星         恆星        日月交點            漢堡虛星        阿拉伯點         二十八宿
 *                                        |
 *                                        |
 *                               +--------+--------+
 *                               |                 |
 *                             [LunarNode]     LunarApsis
 *                           [TRUE/MEAN]       [MEAN/OSCU]
 *                           North/South   PERIGEE (近)/APOGEE (遠)
 */
abstract class AstroPoint(nameKey: String, resource: String, abbrKey: String?, val unicode: Char? = null) : Point(nameKey, resource, abbrKey) {
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
