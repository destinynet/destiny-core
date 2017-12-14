/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:42:03
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

import static java.util.Optional.empty;

/** Partile conjunction with Jupiter or Venus. 
 * 和金星或木星合相，交角 1 度內 */
public final class Partile_Conj_Jupiter_Venus extends Rule {

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h) {
    return h.getPositionOpt(planet).map(Position::getLng).flatMap(planetDegree ->
      h.getPositionOpt(Planet.JUPITER).map(Position::getLng).flatMap(jupiterDeg ->
        h.getPositionOpt(Planet.VENUS).map(Position::getLng).flatMap(venusDeg -> {
          if (planet != Planet.JUPITER && Horoscope.getAngle(planetDegree, jupiterDeg) <= 1) {
            logger.debug("{} 與 {} 形成 {}", planet, Planet.JUPITER, Aspect.CONJUNCTION);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.JUPITER, Aspect.CONJUNCTION}));
          }
          else if (planet != Planet.VENUS && Horoscope.getAngle(planetDegree, venusDeg) <= 1) {
            logger.debug("{} 與 {} 形成 {}", planet, Planet.VENUS, Aspect.CONJUNCTION);
            return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.VENUS, Aspect.CONJUNCTION}));
          }
          return empty();
        })
      ));
  }

}
