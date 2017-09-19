/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:00:33
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

public final class House_12 extends Rule {

  public House_12() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext) {
    if (horoscopeContext.getHouse(planet) == 12) {
      //addComment(Locale.TAIWAN , planet + " 位於 12 宮");
      return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
    }
    return Optional.empty();
  }

}
