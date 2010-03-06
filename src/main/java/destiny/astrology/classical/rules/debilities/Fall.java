/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 4:51:31
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import destiny.astrology.classical.Dignity;

/** In Fall. */
public final class Fall extends EssentialRule
{

  public Fall()
  {
    super("Fall");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    
    if (planet == essentialImpl.getPoint(sign, Dignity.FALL))
    {
      addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 Fall");
      return true;
    }
    return false;
  }

}
