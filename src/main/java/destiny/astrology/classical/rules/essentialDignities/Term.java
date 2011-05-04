/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:26:46
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** A planet in itw own term. */
public final class Term extends Rule
{
  public Term()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == essentialImpl.getTermsPoint(horoscopeContext.getPosition(planet).getLongitude()))
    {
      return new Tuple<String , Object[]>("comment" , new Object[]{planet , horoscopeContext.getPosition(planet).getLongitude()});
    }
    return null;
  }
}
