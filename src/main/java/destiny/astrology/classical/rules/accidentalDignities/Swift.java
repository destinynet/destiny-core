/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:51:44
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.AverageDailyMotionMap;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Swift in motion (faster than average). */
public final class Swift extends Rule
{
  public Swift()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if ( AverageDailyMotionMap.get(planet) != null &&
        horoscopeContext.getPosition(planet).getSpeedLongitude() > AverageDailyMotionMap.get(planet))
    {
      //addComment(Locale.TAIWAN , planet + " 每日移動速度比平均值還快");
      return new Tuple<String , Object[]>("comment" , new Object[] {planet});
    }
    return null;
  }

}
