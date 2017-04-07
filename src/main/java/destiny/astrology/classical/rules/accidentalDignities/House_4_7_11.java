/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:44:17
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
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
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext) {
    int planetHouse = horoscopeContext.getHouse(planet);
    if (planetHouse == 4 || planetHouse == 7 || planetHouse == 11) {
      //planet + " 位於第 " + planetHouse + " 宮 (Good Daemon's) House
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, planetHouse}));
    }
    return Optional.empty();
  }

}
