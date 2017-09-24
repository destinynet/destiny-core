/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:07:11
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/**
 * 喜樂宮 Joy House. 
 * Mercory in 1st. 
 * Moon in 3rd. 
 * Venus in 5th. 
 * Mars in 6th. 
 * Sun in 9th. 
 * Jupiter in 11th. 
 * Saturn in 12th.
 */
public final class JoyHouse extends Rule {

  public JoyHouse()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getHouse(planet)
      .filter(house ->
          (planet == Planet.MERCURY && house == 1) ||
          (planet == Planet.MOON && house == 3) ||
          (planet == Planet.VENUS && house == 5) ||
          (planet == Planet.MARS && house == 6) ||
          (planet == Planet.SUN && house == 9) ||
          (planet == Planet.JUPITER && house == 11) ||
          (planet == Planet.SATURN && house == 12)
      )
      .map(house -> Tuple.tuple("comment", new Object[]{planet, house}));

  }

}
