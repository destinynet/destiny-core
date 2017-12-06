/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:50:48
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile trine Jupiter or Venus. */
public final class Partile_Trine_Jupiter_Venus extends Rule {

  public Partile_Trine_Jupiter_Venus() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet).map(Position::getLng).flatMap(planetDegree ->
      h.getPositionOpt(Planet.JUPITER).map(Position::getLng).flatMap(jupiterDeg ->
        h.getPositionOpt(Planet.VENUS).map(Position::getLng).flatMap(venusDeg -> {
          if (planet != Planet.JUPITER && AspectEffectiveModern.isEffective(planetDegree, jupiterDeg, Aspect.TRINE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}" , planet , Planet.JUPITER , Aspect.TRINE);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.JUPITER, Aspect.TRINE}));
          }
          else if (planet != Planet.VENUS && AspectEffectiveModern.isEffective(planetDegree, venusDeg, Aspect.TRINE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}" , planet , Planet.VENUS , Aspect.TRINE);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.VENUS, Aspect.TRINE}));
          }
          return Optional.empty();
        })
      )
    );

  }

}
