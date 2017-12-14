/**
 * @author smallufo
 * Created on 2008/1/8 at 下午 12:26:42
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.ICollectionOfLight
import destiny.astrology.classical.ICollectionOfLight.CollectType.DIGNITIES
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * 目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮
 */
class Collection_of_Light(private val collectionOfLightImpl: ICollectionOfLight) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return Optional.ofNullable(getResult2(planet , h)?.let { p -> Tuple.tuple(p.first , p.second) })

  }


  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return collectionOfLightImpl.getResult(planet , h , DIGNITIES)?.let { twoPlanets ->
      "comment" to arrayOf(planet, twoPlanets[0], twoPlanets[1], h.getAngle(twoPlanets[0], twoPlanets[1]))
    }
  }
}
