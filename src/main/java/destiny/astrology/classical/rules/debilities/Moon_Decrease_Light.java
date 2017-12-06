/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:19:43
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Moon decreasing in light. */
public final class Moon_Decrease_Light extends Rule {

  public Moon_Decrease_Light() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet)
      .filter(pos -> planet == Planet.MOON).map(Position::getLng).flatMap(moonDegree ->
        h.getPositionOpt(Planet.SUN)
          .map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOriental(moonDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
      );

//    if (planet == Planet.MOON) {
//      double planetDegree = h.getPosition(planet).getLng();
//      double sunDegree = h.getPosition(Planet.SUN).getLng();
//
//      if (Horoscope.isOriental(planetDegree, sunDegree)) {
//        logger.debug("{} 在太陽東邊（月減光/下弦月）");
//        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
//      }
//    }
//    return empty();
  }

}
