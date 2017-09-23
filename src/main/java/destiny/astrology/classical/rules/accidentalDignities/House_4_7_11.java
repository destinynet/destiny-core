/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:44:17
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** In the 7th, 4th, or 11th (Good Daemon's) houses. */
public final class House_4_7_11 extends Rule {

  public House_4_7_11() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    return h.getHouse(planet)
      .filter(house -> house == 4 || house == 7 || house == 11)
      .map(house -> Tuple.tuple("comment", new Object[]{planet, house}));
  }

}
