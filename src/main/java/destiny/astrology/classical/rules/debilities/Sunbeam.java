/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:25:34
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** Under the Sunbeans (between 8.5 and 17 from Sol). */
public final class Sunbeam extends Rule
{

  public Sunbeam()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) > 8.5 &&
          horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) <= 17)
      {
        //addComment(Locale.TAIWAN , planet + " 被太陽曬傷 (Sunbeam)");
        return new Tuple<String , Object[]>("comment" , new Object[]{planet});
      }
    }
    return null;
  }

}
