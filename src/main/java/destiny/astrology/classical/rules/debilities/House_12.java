/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 5:00:33
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class House_12 extends Rule
{
  public House_12()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (horoscopeContext.getHouse(planet)==12)
    {
      //addComment(Locale.TAIWAN , planet + " 位於 12 宮");
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet}));
    }
    return Optional.empty();
  }

}
