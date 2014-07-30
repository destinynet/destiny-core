/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:44:17
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** In the 7th, 4th, or 11th (Good Daemon's) houses. */
public final class House_4_7_11 extends Rule
{
  public House_4_7_11()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet);
    if ( planetHouse == 4 || planetHouse == 7 || planetHouse == 11)
    {
      //planet + " 位於第 " + planetHouse + " 宮 (Good Daemon's) House
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , planetHouse});
    }
    return null;
  }

}
