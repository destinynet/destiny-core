/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:00:33
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

public final class House_12 extends Rule
{
  public House_12()
  {
    super("House_12");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (horoscopeContext.getHouse(planet)==12)
    {
      addComment(Locale.TAIWAN , planet + " 位於 12 宮");
      return true;
    }
    return false;
  }

}
