/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:46:56
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** In the 2nd or 5th house. */
public final class House_2_5 extends Rule
{
  public House_2_5()
  {
    super("House_2_5");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet); 
    if ( planetHouse == 2 || planetHouse == 5)
    {
      addComment(Locale.TAIWAN , planet + " 位於第 " + planetHouse + " 宮");
      return true;
    }
    return false;
  }

}
