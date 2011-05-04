/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:00:33
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

public final class House_12 extends Rule
{
  public House_12()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (horoscopeContext.getHouse(planet)==12)
    {
      //addComment(Locale.TAIWAN , planet + " 位於 12 宮");
      return new Tuple<String , Object[]>("comment" , new Object[]{planet});
    }
    return null;
  }

}
