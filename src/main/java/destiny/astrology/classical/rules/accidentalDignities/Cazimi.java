/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:26:11
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Cazimi (within 17 minutes of the Sun). */
public final class Cazimi extends Rule
{
  public Cazimi()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) < 17.0/60 )
      {
        return new Tuple<>("comment" , new Object[] {planet});
      }
    }
    return null;
  }

}
