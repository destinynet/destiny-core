/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:51:44
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.AverageDailyMotionMap;

/** Swift in motion (faster than average). */
public final class Swift extends Rule
{
  public Swift()
  {
    super("Swift");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if ( AverageDailyMotionMap.get(planet) != null &&
        horoscopeContext.getPosition(planet).getSpeedLongitude() > AverageDailyMotionMap.get(planet))
    {
      addComment(Locale.TAIWAN , planet + " 每日移動速度比平均值還快");
      return true;
    }
    return false;
  }

}
