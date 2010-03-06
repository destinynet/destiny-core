/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:26:46
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** A planet in itw own term. */
public final class Term extends Rule
{
  public Term()
  {
    super("Term");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == essentialImpl.getTermsPoint(horoscopeContext.getPosition(planet).getLongitude()))
    {
      addComment(Locale.TAIWAN , planet + " 位於其 Term : " + horoscopeContext.getPosition(planet).getLongitude());
      return true;
    }
    return false;
  }
}
