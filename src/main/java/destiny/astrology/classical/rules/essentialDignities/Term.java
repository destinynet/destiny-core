/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:26:46
 */
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** A planet in itw own term. */
public final class Term extends Rule {

  public Term() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet)
      .map(Position::getLng)
      .flatMap(lngDeg -> {
        Point termPoint = essentialImpl.getTermsPoint(lngDeg);
        if (planet == termPoint)
          return Optional.of(Tuple.tuple("comment", new Object[]{planet, lngDeg}));
        return Optional.empty();
        }
      );

  }
}
