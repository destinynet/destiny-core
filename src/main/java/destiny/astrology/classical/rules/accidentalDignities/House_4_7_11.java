/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:44:17
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** In the 7th, 4th, or 11th (Good Daemon's) houses. */
public final class House_4_7_11 extends Rule
{
  public House_4_7_11()
  {
    super("House_4_7_11");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet);
    if ( planetHouse == 4 || planetHouse == 7 || planetHouse == 11)
    {
      addComment(Locale.TAIWAN , planet + " 位於第 " + planetHouse + " 宮 (Good Daemon's) House");
      return true;
    }
    return false;
  }

}
