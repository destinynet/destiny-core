/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:19:43
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Moon decreasing in light. */
public final class Moon_Decrease_Light extends Rule
{

  public Moon_Decrease_Light()
  {
    super("Moon_Decrease_Light");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.MOON)
    {
      double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
      double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
      
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        addComment(Locale.TAIWAN , planet + " 在太陽東邊（月減光/下弦月）");
        return true;
      }
    }
    return false;
  }

}
