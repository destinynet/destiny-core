/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:22:25
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Combust the Sun (between 17' and 8.5 from Sol). */
public final class Combustion extends Rule
{
  public Combustion()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) > 17.0/60 &&
          horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) <= 8.5)
      {
        //addComment(Locale.TAIWAN , planet + " 被太陽焦傷 (Combustion)");
        return new Tuple<>("comment" , new Object[]{planet});
      }
    }
    return null;
  }

}
