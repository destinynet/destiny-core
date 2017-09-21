/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:27:33
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

import static java.util.Optional.empty;

/** Partile conjunction with Mars or Saturn. */
public final class Partile_Conj_Mars_Saturn extends Rule {

  public Partile_Conj_Mars_Saturn() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOptional(planet).map(Position::getLng).flatMap(planetDegree ->
      h.getPositionOptional(Planet.MARS).map(Position::getLng).flatMap(marsDeg ->
        h.getPositionOptional(Planet.SATURN).map(Position::getLng).flatMap(saturnDeg -> {
          if (planet != Planet.MARS && Horoscope.getAngle(planetDegree, marsDeg) <= 1) {
            logger.debug("{} 與 {} 形成 {}" , planet , Planet.MARS , Aspect.CONJUNCTION);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.MARS, Aspect.CONJUNCTION}));
          }
          else if (planet != Planet.SATURN && Horoscope.getAngle(planetDegree, saturnDeg) <= 1) {
            logger.debug("{} 與 {} 形成 {}" , planet , Planet.SATURN , Aspect.CONJUNCTION);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.SATURN, Aspect.CONJUNCTION}));
          }
          return empty();
        })
      )
    );

  }

}
