/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:46:40
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** In Detriment.  */
class Detriment : EssentialRule() {

  public override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getZodiacSignOpt(planet)
      .filter { sign -> planet === essentialImpl.getPoint(sign, Dignity.DETRIMENT).orElse(null) }
      .map { sign -> Tuple.tuple("comment", arrayOf(planet, sign)) }
  }

  public override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)
      ?.takeIf { sign -> planet === essentialImpl.getPoint(sign, Dignity.DETRIMENT).orElse(null) }
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
