/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:44:04
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.FixedStar;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000. */
public final class Conj_Algol extends Rule
{
  public Conj_Algol()
  {
    super("Conj_Algol");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double algolDeg = horoscopeContext.getPosition(FixedStar.ALGOL).getLongitude();
    
    if (AspectEffectiveModern.isEffective(planetDegree , algolDeg , Aspect.CONJUNCTION , 5))
    {
      addComment(Locale.TAIWAN , planet + " 與 " + FixedStar.ALGOL + " 形成 " + Aspect.CONJUNCTION);
      return true;
    }
    return false;
  }

}
