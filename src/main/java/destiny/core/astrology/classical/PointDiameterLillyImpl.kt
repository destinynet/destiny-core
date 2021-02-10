/**
 * @author smallufo
 * Created on 2007/11/25 at 上午 12:21:59
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Planet
import destiny.core.astrology.Point
import java.io.Serializable
import java.util.*

/**
 * 古典占星術，William Lilly 的交角 <br></br>
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 比較少用，對比於內定的 PointDiameterAlBiruniImpl
 */
class PointDiameterLillyImpl : IPointDiameter, Serializable {

  override fun toString(locale: Locale): String {
    return "William Lilly"
  }

  override fun getDescription(locale: Locale): String {
    return "William Lilly"
  }

  override fun getDiameter(point: Point): Double {
    return planetOrbsMap[point] ?: 2.0
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PointDiameterLillyImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    private val planetOrbsMap = mapOf(
      Planet.SUN to 17.0,
      Planet.MOON to 12.5,
      Planet.MERCURY to 7.0,
      Planet.VENUS to 8.0,
      Planet.MARS to 7.5,
      Planet.JUPITER to 12.0,
      Planet.SATURN to 10.0,
      Planet.URANUS to 5.0,
      Planet.NEPTUNE to 5.0,
      Planet.PLUTO to 5.0
    )
  }
}
