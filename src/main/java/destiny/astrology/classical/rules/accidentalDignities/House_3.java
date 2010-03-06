/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:49:19
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** In the 3rd house. */
public final class House_3 extends Rule
{
  public House_3()
  {
    super("House_3");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet);
    if ( planetHouse == 3)
    {
      addComment(Locale.TAIWAN , planet + " 位於第 " + planetHouse + " 宮");
      return true;
    }
    return false;
  }

}
