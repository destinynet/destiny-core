/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:08:31
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.AverageDailyMotionMap;

public final class Slower extends Rule
{
  public Slower()
  {
    super("Slower");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (AverageDailyMotionMap.get(planet) != null &&
        horoscopeContext.getPosition(planet).getSpeedLongitude() < AverageDailyMotionMap.get(planet))
    {
      addComment(Locale.TAIWAN , planet + " 每日移動速度比平均值還慢");
      return true;
    }
    return false;
  }

}
