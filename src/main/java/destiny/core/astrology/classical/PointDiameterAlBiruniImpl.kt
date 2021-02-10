/**
 * @author smallufo
 * Created on 2007/11/25 at 上午 12:19:49
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Planet.*
import destiny.core.astrology.Point
import java.io.Serializable
import java.util.*


/**
 * 古典占星術，Al-Biruni 的交角
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 尚有 PointDiameterLillyImpl 的實作可供比對
 */
class PointDiameterAlBiruniImpl : IPointDiameter, Serializable {

  override fun toString(locale: Locale): String {
    return "Al-Biruni"
  }

  override fun getDescription(locale: Locale): String {
    return "Al-Biruni"
  }

  override fun getDiameter(point: Point): Double {
    return planetOrbsMap[point] ?: 2.0
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PointDiameterAlBiruniImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    private val planetOrbsMap = mapOf(
      SUN to 15.0,
      MOON to 12.0,
      MERCURY to 7.0,
      VENUS to 7.0,
      MARS to 8.0,
      JUPITER to 9.0,
      SATURN to 9.0,
      URANUS to 5.0,
      NEPTUNE to 5.0,
      PLUTO to 5.0
    )
  }

}
