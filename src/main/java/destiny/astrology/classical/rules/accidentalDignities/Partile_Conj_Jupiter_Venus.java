/**
 * @author smallufo 
 * Created on 2007/12/29 at 下午 11:42:03
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.Aspect;
import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Partile conjunction with Jupiter or Venus. */
public final class Partile_Conj_Jupiter_Venus extends Rule
{
  public Partile_Conj_Jupiter_Venus()
  {
    super("Partile_Conj_Jupiter_Venus");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double jupiterDeg = horoscopeContext.getPosition(Planet.JUPITER).getLongitude();
    double venusDeg   = horoscopeContext.getPosition(Planet.VENUS).getLongitude();
    
    if (planet != Planet.JUPITER && Horoscope.getAngle(planetDegree , jupiterDeg) <= 1) 
    {
      addComment(Locale.TAIWAN , planet + " 與 " + Planet.JUPITER + " 形成 " + Aspect.CONJUNCTION);
      return true;
    }
    else if (planet != Planet.VENUS && Horoscope.getAngle(planetDegree , venusDeg) <= 1)
    {
      addComment(Locale.TAIWAN , planet + " 與 " + Planet.VENUS + " 形成 " + Aspect.CONJUNCTION);
      return true;
    }
    return false;
  }

}
