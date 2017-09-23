/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:02:49
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

public final class House_6_8 extends Rule {

  public House_6_8() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    return h.getHouse(planet)
      .filter(house -> house == 6 || house == 8)
      .map(house -> Tuple.tuple("comment", new Object[]{planet, house}));
  }

}
