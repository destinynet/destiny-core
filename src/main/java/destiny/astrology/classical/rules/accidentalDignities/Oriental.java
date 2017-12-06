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

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun.
 * 火星、木星、土星 是否 東出 於 太陽
 * */
public final class Oriental extends Rule {

  public Oriental() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet)
      .filter(pos -> planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
      .map(Position::getLng).flatMap(planetDegree ->
        h.getPositionOpt(Planet.SUN)
          .map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOriental(planetDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
    );
  }

}
