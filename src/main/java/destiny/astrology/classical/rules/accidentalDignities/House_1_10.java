/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:39:42
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** In the 10th or 1st house. */
public final class House_1_10 extends Rule {

  public House_1_10() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    int planetHouse = h.getHouse(planet);
    if (planetHouse == 1 || planetHouse == 10) {
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, planetHouse}));
    }
    return Optional.empty();
  }

}
