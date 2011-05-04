/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:05:56
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

public final class Retrograde extends Rule
{
  public Retrograde()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (horoscopeContext.getPosition(planet).getSpeedLongitude() < 0)
    {
      //addComment(Locale.TAIWAN , planet + " 逆行");
      return new Tuple<String , Object[]>("comment" , new Object[]{planet});
    }
    return null;
  }

}
