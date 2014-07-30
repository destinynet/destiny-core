/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:08:31
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.AverageDailyMotionMap;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Slower extends Rule
{
  public Slower()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (AverageDailyMotionMap.get(planet) != null &&
        horoscopeContext.getPosition(planet).getSpeedLongitude() < AverageDailyMotionMap.get(planet))
    {
      //addComment(Locale.TAIWAN , planet + " 每日移動速度比平均值還慢");
      return new Tuple<String , Object[]>("comment" , new Object[]{planet});
    }
    return null;
  }

}
