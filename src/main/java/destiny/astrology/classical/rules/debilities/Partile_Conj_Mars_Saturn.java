/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:27:33
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Aspect;
import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Partile conjunction with Mars or Saturn. */
public final class Partile_Conj_Mars_Saturn extends Rule
{

  public Partile_Conj_Mars_Saturn()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double marsDeg = horoscopeContext.getPosition(Planet.MARS).getLongitude();
    double saturnDeg = horoscopeContext.getPosition(Planet.SATURN).getLongitude();
    
    if (planet != Planet.MARS && Horoscope.getAngle(planetDegree , marsDeg) <= 1)
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.MARS + " 形成 " + Aspect.CONJUNCTION);
      return new Tuple<>("comment" , new Object[]{planet , Planet.MARS , Aspect.CONJUNCTION});
    }
    else if (planet != Planet.SATURN && Horoscope.getAngle(planetDegree , saturnDeg) <= 1)
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.SATURN + " 形成 " + Aspect.CONJUNCTION);
      return new Tuple<>("comment" , new Object[]{planet , Planet.SATURN, Aspect.CONJUNCTION});
    }
    return null;
  }

}
