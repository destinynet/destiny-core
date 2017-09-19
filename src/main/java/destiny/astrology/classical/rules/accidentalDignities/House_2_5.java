/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:46:56
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** In the 2nd or 5th house. */
public final class House_2_5 extends Rule {

  public House_2_5() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext) {
    int planetHouse = horoscopeContext.getHouse(planet);
    if (planetHouse == 2 || planetHouse == 5) {
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, planetHouse}));
    }
    return Optional.empty();
  }

}
