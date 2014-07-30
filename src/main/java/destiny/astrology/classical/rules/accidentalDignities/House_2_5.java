/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:46:56
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** In the 2nd or 5th house. */
public final class House_2_5 extends Rule
{
  public House_2_5()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet); 
    if ( planetHouse == 2 || planetHouse == 5)
    {
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , planetHouse});
    }
    return null;
  }

}
