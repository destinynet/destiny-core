/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:50:05
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Direct in motion (does not apply to Sun and Moon). */
public final class Direct extends Rule
{
  public Direct()
  {
    super("Direct");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN && planet != Planet.MOON)
    {
      if(horoscopeContext.getPosition(planet).getSpeedLongitude() > 0)
      {
        addComment(Locale.TAIWAN , planet + " 是 DIRECT 移動 (順行)");
        return true;
      }
    }
    return false;
  }

}
