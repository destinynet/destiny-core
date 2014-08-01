/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:24:40
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !? */
public final class Free_Combustion extends Rule
{
  public Free_Combustion()
  {
  }

  @Override
  protected Optional<Tuple<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) > 17)
      {
        //addComment(Locale.TAIWAN , planet + " 遠離太陽焦傷");
        return Optional.of(Tuple.of("comment" , new Object[] {planet}));
      }
    }
    return Optional.empty();
  }
}
