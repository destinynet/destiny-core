/**
 * @author smallufo
 * Created on 2008/1/8 at 下午 12:26:42
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.ICollectionOfLight;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/**
 * 目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮
 */
public final class Collection_of_Light extends Rule
{
  private final ICollectionOfLight collectionOfLightImpl;

  public Collection_of_Light(ICollectionOfLight collectionOfLightImpl) {
    this.collectionOfLightImpl = collectionOfLightImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h) {
    return collectionOfLightImpl.getResult(planet , h , ICollectionOfLight.CollectType.DIGNITIES).map(twoPlanets ->
      Tuple.tuple("comment", new Object[]{planet, twoPlanets.get(0), twoPlanets.get(1), h.getAngle(twoPlanets.get(0), twoPlanets.get(1))})
    );
  }

}
