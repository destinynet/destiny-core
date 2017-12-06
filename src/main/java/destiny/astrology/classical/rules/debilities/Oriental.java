/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:16:57
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
 * Mercury, or Venus oriental to the Sun.
 * 金星、水星，是否 東出 於 太陽
 * */
public final class Oriental extends Rule {

  public Oriental() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet)
      .filter(pos -> planet == Planet.MERCURY || planet == Planet.VENUS)
      .map(Position::getLng).flatMap(planetDegree ->
        h.getPositionOpt(Planet.SUN)
          .map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOriental(planetDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
      );
  }

}
