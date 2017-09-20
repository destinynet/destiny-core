/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:10:06
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun. */
public final class Oriental extends Rule
{
  public Oriental()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h)
  {
    double planetDegree = h.getPosition(planet).getLng();
    double sunDegree    = h.getPosition(Planet.SUN).getLng();
    
    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
    {
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        // addComment(Locale.TAIWAN , planet + " 在太陽東邊");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
