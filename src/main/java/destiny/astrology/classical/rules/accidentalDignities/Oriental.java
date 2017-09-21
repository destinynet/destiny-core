/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:10:06
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun. */
public final class Oriental extends Rule {

  public Oriental() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOptional(planet)
      .filter(pos -> (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN))
      .map(Position::getLng).flatMap(planetDegree ->
        h.getPositionOptional(Planet.SUN).map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOriental(planetDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
    );

//    double planetDegree = h.getPosition(planet).getLng();
//    double sunDegree = h.getPosition(Planet.SUN).getLng();
//
//    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN) {
//      if (Horoscope.isOriental(planetDegree, sunDegree)) {
//        logger.debug("{} 在太陽東邊" , planet);
//        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
//      }
//    }
//    return Optional.empty();
  }

}
