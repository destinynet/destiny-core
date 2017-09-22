/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:13:56
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/**
 * Mars, Jupiter, or Saturn occidental to the Sun.
 * 火星、木星、或土星，在太陽西方
 *  */
public final class Occidental extends Rule {

  public Occidental() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet)
      .filter(pos -> planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
      .map(Position::getLng).flatMap(planetDegree ->
        h.getPosition(Planet.SUN)
          .map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOccidental(planetDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
    );

//    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN) {
//      double planetDegree = h.getPosition(planet).getLng();
//      double sunDegree = h.getPosition(Planet.SUN).getLng();
//      if (Horoscope.isOccidental(planetDegree, sunDegree)) {
//        //addComment(Locale.TAIWAN , planet + " 在太陽西邊");
//        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
//      }
//    }
//    return Optional.empty();
  }

}
