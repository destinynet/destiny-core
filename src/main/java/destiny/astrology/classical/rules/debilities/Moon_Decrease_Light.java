/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:19:43
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Moon decreasing in light. */
public final class Moon_Decrease_Light extends Rule
{

  public Moon_Decrease_Light()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.MOON)
    {
      double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
      double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
      
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        //addComment(Locale.TAIWAN , planet + " 在太陽東邊（月減光/下弦月）");
        return Optional.of(ImmutablePair.of("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
