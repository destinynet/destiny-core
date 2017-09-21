/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:05:56
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

public final class Retrograde extends Rule {

  public Retrograde() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet)
      .map(Position::getSpeedLng)
      .filter(speedLng -> speedLng < 0)
      .map(speedLng -> Tuple.tuple("comment", new Object[]{planet}));

//    if (h.getPosition(planet).getSpeedLng() < 0) {
//      logger.debug("{} 逆行" , planet);
//      return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
//    }
//    return Optional.empty();
  }

}
