/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:10:06
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun. */
public final class Oriental extends Rule
{
  public Oriental()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
    
    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
    {
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        // addComment(Locale.TAIWAN , planet + " 在太陽東邊");
        return new Tuple<>("comment" , new Object[] {planet});
      }
    }
    return null;
  }

}
