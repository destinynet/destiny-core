/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:10:06
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun. */
public final class Oriental extends Rule
{
  public Oriental()
  {
    super("Oriental");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
    
    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
    {
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        addComment(Locale.TAIWAN , planet + " 在太陽東邊");
        return true;
      }
    }
    return false;
  }

}
