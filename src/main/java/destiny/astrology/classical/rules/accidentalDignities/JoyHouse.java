/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:07:11
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** 
 * 喜樂宮 Joy House. 
 * Mercory in 1st. 
 * Moon in 3rd. 
 * Venus in 5th. 
 * Mars in 6th. 
 * Sun in 9th. 
 * Jupiter in 11th. 
 * Saturn in 12th.
 */
public final class JoyHouse extends Rule
{
  public JoyHouse()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet);
    
    if ((planet == Planet.MERCURY && planetHouse == 1) ||
        (planet == Planet.MOON && planetHouse == 3) ||
        (planet == Planet.VENUS && planetHouse == 5) ||
        (planet == Planet.MARS && planetHouse == 6) ||
        (planet == Planet.SUN && planetHouse == 9) ||
        (planet == Planet.JUPITER && planetHouse == 11) ||
        (planet == Planet.SATURN && planetHouse == 12)
       )
    {
      //planet + " 落入第 " + planetHouse + " 宮 , 為其喜樂宮 (Joy House)"
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , planetHouse});
    }
    return null;
  }

}
