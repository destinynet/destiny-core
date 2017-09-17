/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:19:43
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Horoscope2;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Moon decreasing in light. */
public final class Moon_Decrease_Light extends Rule {

  public Moon_Decrease_Light() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext) {
    if (planet == Planet.MOON) {
      double planetDegree = horoscopeContext.getPosition(planet).getLng();
      double sunDegree = horoscopeContext.getPosition(Planet.SUN).getLng();

      if (Horoscope2.isOriental(planetDegree, sunDegree)) {
        //addComment(Locale.TAIWAN , planet + " 在太陽東邊（月減光/下弦月）");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
