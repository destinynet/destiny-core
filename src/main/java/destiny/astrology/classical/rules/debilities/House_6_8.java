/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:02:49
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class House_6_8 extends Rule
{

  public House_6_8()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (horoscopeContext.getHouse(planet) == 6)
    {
      //addComment(Locale.TAIWAN , planet + " 位於 6 或 8 宮");
      return new Tuple<>("comment" , new Object[]{planet , 6});
    }
    else if (horoscopeContext.getHouse(planet) ==8)
    {
      return new Tuple<>("comment" , new Object[]{planet , 8});
    }
    return null;
  }

}
