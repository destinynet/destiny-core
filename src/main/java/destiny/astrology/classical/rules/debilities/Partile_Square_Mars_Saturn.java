/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:42:01
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

import static java.util.Optional.empty;

/** Partile square Mars or Saturn. */
public final class Partile_Square_Mars_Saturn extends Rule {

  public Partile_Square_Mars_Saturn() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet).map(Position::getLng).flatMap(planetDegree ->
      h.getPosition(Planet.MARS).map(Position::getLng).flatMap(marsDeg ->
        h.getPosition(Planet.SATURN).map(Position::getLng).flatMap(saturnDeg -> {
          if (planet != Planet.MARS && AspectEffectiveModern.isEffective(planetDegree, marsDeg, Aspect.SQUARE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}" , planet , Planet.MARS , Aspect.SQUARE);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.MARS, Aspect.SQUARE}));
          }
          else if (planet != Planet.SATURN && AspectEffectiveModern.isEffective(planetDegree, saturnDeg, Aspect.SQUARE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}" , planet , Planet.SATURN , Aspect.SQUARE);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.SATURN, Aspect.SQUARE}));
          }
          return empty();
        })
      )
    );

  }

}
