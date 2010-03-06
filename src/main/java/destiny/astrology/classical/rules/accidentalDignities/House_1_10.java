/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:39:42
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** In the 10th or 1st house. */
public final class House_1_10 extends Rule
{
  public House_1_10()
  {
    super("House_1_10");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet); 
    if ( planetHouse == 1 || planetHouse == 10)
    {
      addComment(Locale.TAIWAN , planet + " 位於第 " + planetHouse + " 宮");
      return true;
    }
    return false;
  }

}
