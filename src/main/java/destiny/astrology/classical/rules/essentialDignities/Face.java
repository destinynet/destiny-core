/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:29:56
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** A planet in its own Chaldean decanate or face. */
public final class Face extends Rule
{
  public Face()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == essentialImpl.getFacePoint(horoscopeContext.getPosition(planet).getLongitude()))
    {
      //addComment(Locale.TAIWAN , planet + " 位於其 Chaldean decanate or face : "+ horoscopeContext.getPosition(planet).getLongitude());
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , horoscopeContext.getPosition(planet).getLongitude()});
    }
    return null;
  }
}
