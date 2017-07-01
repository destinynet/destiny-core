/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:20:28
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Mercury, or Venus occidental of (rising after) the Sun. */
public final class Occidental extends Rule
{
  public Occidental()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLng();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLng();
    
    if (planet == Planet.MERCURY || planet == Planet.VENUS)
    {
      if ( Horoscope.isOccidental(planetDegree , sunDegree))
      {
        //addComment(Locale.TAIWAN , planet + " 在太陽西邊");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
