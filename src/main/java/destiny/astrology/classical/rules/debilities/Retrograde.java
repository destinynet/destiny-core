/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:05:56
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class Retrograde extends Rule
{
  public Retrograde()
  {
  }

  @Override
  protected Optional<Tuple<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (horoscopeContext.getPosition(planet).getSpeedLongitude() < 0)
    {
      //addComment(Locale.TAIWAN , planet + " 逆行");
      return Optional.of(Tuple.of("comment" , new Object[]{planet}));
    }
    return Optional.empty();
  }

}
