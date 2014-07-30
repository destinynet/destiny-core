/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:48:01
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** In the 9th house. */
public final class House_9 extends Rule
{
  public House_9()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet);
    if ( planetHouse == 9)
    {
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , planetHouse});
    }
    return null;
  }

}
