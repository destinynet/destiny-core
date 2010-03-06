/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:48:01
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** In the 9th house. */
public final class House_9 extends Rule
{
  public House_9()
  {
    super("House_9");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet);
    if ( planetHouse == 9)
    {
      addComment(Locale.TAIWAN , planet + " 位於第 " + planetHouse + " 宮");
      return true;
    }
    return false;
  }

}
