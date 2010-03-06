/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 4:46:40
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import destiny.astrology.classical.Dignity;

/** In Detriment. */
public final class Detriment extends EssentialRule
{
  public Detriment()
  {
    super("Detriment");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    
    if (planet == essentialImpl.getPoint(sign, Dignity.DETRIMENT))
    {
      addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 Detriment");
      return true;
    }
    return false;
  }

}
