/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:22:44
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Moon increasing in light (月增光/上弦月) , or occidental of the Sun. */
public final class Moon_Increase_Light extends Rule {

  public Moon_Increase_Light() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet)
      .filter(pos -> planet == Planet.MOON).map(Position::getLng).flatMap(moonDegree ->
        h.getPositionOpt(Planet.SUN)
          .map(Position::getLng)
          .filter(sunDegree -> Horoscope.isOccidental(moonDegree, sunDegree))
          .map(sunDegree -> Tuple.tuple("comment", new Object[]{planet}))
    );
  }

}
