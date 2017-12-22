/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:19:56
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities).  */
class Triplicity(
  /** 計算白天黑夜的實作  */
  private val dayNightImpl: DayNightDifferentiator) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val sign = h.getZodiacSign(planet)
    return sign?.let {
      val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)
      if (dayNight == DayNight.DAY && planet ===  triplicityImpl.getPoint(sign , DayNight.DAY) ||
          dayNight == DayNight.NIGHT && planet === triplicityImpl.getPoint(sign , DayNight.NIGHT) ) {
        logger.debug("{} 位於 {} 為其 {} 之 Triplicity" , planet , sign , dayNight)
        return@let "comment" to arrayOf(planet, sign, dayNight)
      } else
        return@let null
    }
  }
}
