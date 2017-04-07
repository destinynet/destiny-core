/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:49:19
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** In the 3rd house. */
public final class House_3 extends Rule {

  public House_3() {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext) {
    int planetHouse = horoscopeContext.getHouse(planet);
    if (planetHouse == 3) {
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet, planetHouse}));
    }
    return Optional.empty();
  }

}
