/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:38:12
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Partile opposite Mars or Saturn. */
public final class Partile_Oppo_Mars_Saturn extends Rule
{
  public Partile_Oppo_Mars_Saturn()
  {
    super("Partile_Oppo_Mars_Saturn");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double marsDeg = horoscopeContext.getPosition(Planet.MARS).getLongitude();
    double saturnDeg = horoscopeContext.getPosition(Planet.SATURN).getLongitude();
    
    if ( planet != Planet.MARS && AspectEffectiveModern.isEffective( planetDegree , marsDeg , Aspect.OPPOSITION , 1.0))
    {
      addComment(Locale.TAIWAN , planet + " 與 " + Planet.MARS + " 形成 " + Aspect.OPPOSITION);
      return true;
    }
    else if ( planet != Planet.SATURN && AspectEffectiveModern.isEffective( planetDegree , saturnDeg , Aspect.OPPOSITION, 1.0))
    {
      addComment(Locale.TAIWAN , planet + " 與 " + Planet.SATURN + " 形成 " + Aspect.OPPOSITION);
      return true;
    }
    return false;
  }

}
