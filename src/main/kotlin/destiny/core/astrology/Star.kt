/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 5:18:37
 */
package destiny.core.astrology

import destiny.tools.Lang
import destiny.core.IPoints
import destiny.core.Point
import destiny.tools.serializers.astrology.StarSerializer
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

/**
 * 代表「星體 (celestial bodies)」 的抽象 class , 星體包括：
 * 恆星 [FixedStar]
 * 行星 [Planet] , 含日、月、冥王星
 * 小行星 [Asteroid]
 * 漢堡派八虛星 [Hamburger]
 * 日月交點 [LunarPoint]
 * <pre>
 * 目前繼承圖如下 :
 *                                      [Star]
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
 *
 *</pre>
 */
@Serializable(with = StarSerializer::class)
sealed class Star(nameKey: String, abbrKey: String?, resource: String, unicode: Char? = null) : AstroPoint(nameKey, resource, abbrKey, unicode) {

  constructor(nameKey: String, resource: String) : this(nameKey, null, resource, null)

  companion object : IPoints<Star> {

    override val type: KClass<out Point> = Star::class

    override val values: Array<Star> by lazy {
      arrayOf(*Planet.values, *Asteroid.values, *FixedStar.values, *LunarPoint.values, *Hamburger.values, *Lot.values, *LunarStation.values)
    }

    override fun fromString(value: String, lang: Lang): Star? {
      return Planet.fromString(value, lang)
        ?: Asteroid.fromString(value, lang)
        ?: FixedStar.fromString(value, lang)
        ?: LunarPoint.fromString(value, lang)
        ?: Hamburger.fromString(value, lang)
        ?: Lot.fromString(value, lang)
        ?: LunarStation.fromString(value, lang)
    }
  }
}
