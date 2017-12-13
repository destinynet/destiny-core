/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:20:28
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Mercury, or Venus occidental of (rising after) the Sun. */
public final class Occidental extends Rule {

  public Occidental() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet)
      .filter(pos -> planet == Planet.MERCURY || planet == Planet.VENUS)
      .map(Position::getLng).flatMap(planetDegree ->
        h.getPositionOpt(Planet.SUN).map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOccidental(planetDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
      );
  }

}
