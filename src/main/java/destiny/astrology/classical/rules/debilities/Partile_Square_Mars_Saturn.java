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

/** Partile square Mars or Saturn. */
public final class Partile_Square_Mars_Saturn extends Rule {

  public Partile_Square_Mars_Saturn() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext) {
    double planetDegree = horoscopeContext.getPosition(planet).getLng();
    double marsDeg = horoscopeContext.getPosition(Planet.MARS).getLng();
    double saturnDeg = horoscopeContext.getPosition(Planet.SATURN).getLng();

    if (planet != Planet.MARS && AspectEffectiveModern.isEffective(planetDegree, marsDeg, Aspect.SQUARE, 1.0)) {
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.MARS, Aspect.SQUARE}));
    }
    else if (planet != Planet.SATURN && AspectEffectiveModern.isEffective(planetDegree, saturnDeg, Aspect.SQUARE, 1.0)) {
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.SATURN, Aspect.SQUARE}));
    }
    return Optional.empty();
  }

}
