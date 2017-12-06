/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:29:56
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

import static java.util.Optional.empty;

/** A planet in its own Chaldean decanate or face. */
public final class Face extends Rule {

  public Face() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOpt(planet).map(Position::getLng).flatMap(lngDeg -> {
        Point facePoint = essentialImpl.getFacePoint(lngDeg);
        if (planet == facePoint) {
          logger.debug("{} 位於其 Chaldean decanate or face : {}" , planet , lngDeg);
          return Optional.of(Tuple.tuple("comment", new Object[]{planet, lngDeg}));
        }
        return empty();
      }
    );

//    if (planet == essentialImpl.getFacePoint(h.getPosition(planet).getLng())) {
//      logger.debug("{} 位於其 Chaldean decanate or face : {}" , planet , h.getPosition(planet).getLng());
//      return Optional.of(Tuple.tuple("comment", new Object[]{planet, h.getPosition(planet).getLng()}));
//    }
//    return empty();
  }
}
