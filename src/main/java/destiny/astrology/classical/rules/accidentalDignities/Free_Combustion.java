/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:24:40
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !? */
public final class Free_Combustion extends Rule
{
  public Free_Combustion()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) > 17)
      {
        //addComment(Locale.TAIWAN , planet + " 遠離太陽焦傷");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }
}
