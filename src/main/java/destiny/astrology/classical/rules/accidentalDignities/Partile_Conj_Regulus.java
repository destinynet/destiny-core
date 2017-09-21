/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:00:27
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000. */
public final class Partile_Conj_Regulus extends Rule {

  public Partile_Conj_Regulus() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet).map(Position::getLng).flatMap(planetDegree ->
      h.getPosition(FixedStar.REGULUS).map(Position::getLng).flatMap(regulusDeg -> {
        if (AspectEffectiveModern.isEffective(planetDegree, regulusDeg, Aspect.CONJUNCTION, 1)) {
          logger.debug("{} 與 {} 形成 {}" , planet , FixedStar.REGULUS , Aspect.CONJUNCTION);
          return Optional.of(Tuple.tuple("comment", new Object[]{planet, FixedStar.REGULUS, Aspect.CONJUNCTION}));
        }
        return Optional.empty();
      })
    );
  }

}
