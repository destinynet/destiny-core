/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:56:41
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile sextile Jupiter or Venus. */
public final class Partile_Sextile_Jupiter_Venus extends Rule {

  public Partile_Sextile_Jupiter_Venus() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet).map(Position::getLng).flatMap(planetDegree ->
      h.getPosition(Planet.JUPITER).map(Position::getLng).flatMap(jupiterDeg ->
        h.getPosition(Planet.VENUS).map(Position::getLng).flatMap(venusDeg -> {
          if (planet != Planet.JUPITER && AspectEffectiveModern.isEffective(planetDegree, jupiterDeg, Aspect.SEXTILE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, Planet.JUPITER, Aspect.SEXTILE);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.JUPITER, Aspect.SEXTILE}));
          }
          else if (planet != Planet.VENUS && AspectEffectiveModern.isEffective(planetDegree, venusDeg, Aspect.SEXTILE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, Planet.VENUS, Aspect.SEXTILE);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.VENUS, Aspect.SEXTILE}));
          }
          return Optional.empty();
        })
      )
    );

  }

}
