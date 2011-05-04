/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:10:06
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun. */
public final class Oriental extends Rule
{
  public Oriental()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
    
    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
    {
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        // addComment(Locale.TAIWAN , planet + " 在太陽東邊");
        return new Tuple<String , Object[]>("comment" , new Object[] {planet});
      }
    }
    return null;
  }

}
