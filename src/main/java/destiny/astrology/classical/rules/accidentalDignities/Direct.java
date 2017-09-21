/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:50:05
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Direct in motion (does not apply to Sun and Moon). */
public final class Direct extends Rule {

  public Direct() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    if (planet != Planet.SUN && planet != Planet.MOON) {

      return h.getPositionOptional(planet)
        .map(Position::getSpeedLng)
        .filter(speedLng -> speedLng > 0)
        .map(speedLng -> Tuple.tuple("comment", new Object[]{planet}));

    }
    return Optional.empty();
  }

}
