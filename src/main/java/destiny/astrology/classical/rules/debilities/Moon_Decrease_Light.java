/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:19:43
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Moon decreasing in light. */
public final class Moon_Decrease_Light extends Rule
{

  public Moon_Decrease_Light()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.MOON)
    {
      double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
      double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
      
      if ( Horoscope.isOriental(planetDegree , sunDegree))
      {
        //addComment(Locale.TAIWAN , planet + " 在太陽東邊（月減光/下弦月）");
        return new Tuple<>("comment" , new Object[]{planet});
      }
    }
    return null;
  }

}
