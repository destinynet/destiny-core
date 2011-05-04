/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:20:28
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** Mercury, or Venus occidental of (rising after) the Sun. */
public final class Occidental extends Rule
{
  public Occidental()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
    
    if (planet == Planet.MERCURY || planet == Planet.VENUS)
    {
      if ( Horoscope.isOccidental(planetDegree , sunDegree))
      {
        //addComment(Locale.TAIWAN , planet + " 在太陽西邊");
        return new Tuple<String , Object[]>("comment" , new Object[] {planet});
      }
    }
    return null;
  }

}
