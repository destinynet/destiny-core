/**
 * @author smallufo
 * Created on 2007/11/25 at 上午 12:19:49
 */
package destiny.astrology.classical

import destiny.astrology.Planet
import destiny.astrology.Point
import java.io.Serializable
import java.util.*

/**
 * 古典占星術，Al-Biruni 的交角 <br></br>
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 尚有 PointDiameterLillyImpl 的實作可供比對
 */
class PointDiameterAlBiruniImpl : IPointDiameter, Serializable {

  override fun getTitle(locale: Locale): String {
    return "Al-Biruni"
  }

  override fun getDescription(locale: Locale): String {
    return "Al-Biruni"
  }

  override fun getDiameter(point: Point): Double {
    return planetOrbsMap[point]?:2.0
  }

  companion object {
    private val planetOrbsMap = mapOf(
      Planet.SUN to 15.0,
      Planet.MOON to 12.0,
      Planet.MERCURY to 7.0,
      Planet.VENUS to 7.0,
      Planet.MARS to 8.0,
      Planet.JUPITER to 9.0,
      Planet.SATURN to 9.0,
      Planet.URANUS to 5.0,
      Planet.NEPTUNE to 5.0,
      Planet.PLUTO to 5.0
    )
  }
}
