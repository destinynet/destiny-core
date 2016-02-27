/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:29:56
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** A planet in its own Chaldean decanate or face. */
public final class Face extends Rule
{
  public Face()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet == essentialImpl.getFacePoint(horoscopeContext.getPosition(planet).getLongitude()))
    {
      //addComment(Locale.TAIWAN , planet + " 位於其 Chaldean decanate or face : "+ horoscopeContext.getPosition(planet).getLongitude());
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet, horoscopeContext.getPosition(planet).getLongitude()}));
    }
    return Optional.empty();
  }
}
