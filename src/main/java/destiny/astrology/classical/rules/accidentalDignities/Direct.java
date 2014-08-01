/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:50:05
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Direct in motion (does not apply to Sun and Moon). */
public final class Direct extends Rule
{
  public Direct()
  {
  }

  @Override
  protected Optional<Tuple<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN && planet != Planet.MOON)
    {
      if(horoscopeContext.getPosition(planet).getSpeedLongitude() > 0)
      {
        //addComment(Locale.TAIWAN , planet + " 是 DIRECT 移動 (順行)");
        return Optional.of(Tuple.of("comment" , new Object[] {planet}));
      }
    }
    return Optional.empty();
  }

}
