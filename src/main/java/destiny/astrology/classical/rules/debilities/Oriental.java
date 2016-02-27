/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:16:57
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Mercury, or Venus oriental to the Sun. */
public final class Oriental extends Rule
{

  public Oriental()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.MERCURY || planet == Planet.VENUS)
    {
      double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
      double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
      
      if (Horoscope.isOriental(planetDegree , sunDegree))
      {
        //addComment(Locale.TAIWAN , planet + " 在太陽東邊");
        return Optional.of(ImmutablePair.of("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
