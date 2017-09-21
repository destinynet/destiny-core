/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:02:30
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/** Partile conjunct Spica at 23deg50' Libra in January 2000. */
public final class Partile_Conj_Spica extends Rule {

  private Logger logger = LoggerFactory.getLogger(getClass());

  public Partile_Conj_Spica() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    Optional<Double> planetDegree = h.getPosition(planet).map(Position::getLng);
    Optional<Double> spicaDeg = h.getPosition(FixedStar.SPICA).map(Position::getLng);

    if (planetDegree.isPresent() && spicaDeg.isPresent()) {
      if (AspectEffectiveModern.isEffective(planetDegree.get(), spicaDeg.get(), Aspect.CONJUNCTION, 1)) {
        logger.debug("{} 與 {} 形成 {}", planet, FixedStar.SPICA, Aspect.CONJUNCTION);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, FixedStar.SPICA, Aspect.CONJUNCTION}));
      }
    }

    return Optional.empty();
  }

}
