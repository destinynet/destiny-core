/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:16:57
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Mercury, or Venus oriental to the Sun. */
public final class Oriental extends Rule {

  public Oriental() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    if (planet == Planet.MERCURY || planet == Planet.VENUS) {
      double planetDegree = h.getPosition(planet).getLng();
      double sunDegree = h.getPosition(Planet.SUN).getLng();

      if (Horoscope.isOriental(planetDegree, sunDegree)) {
        //addComment(Locale.TAIWAN , planet + " 在太陽東邊");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
