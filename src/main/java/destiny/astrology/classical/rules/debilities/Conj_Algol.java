/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:44:04
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000. */
public final class Conj_Algol extends Rule
{
  public Conj_Algol()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double algolDeg = horoscopeContext.getPosition(FixedStar.ALGOL).getLongitude();
    
    if (AspectEffectiveModern.isEffective(planetDegree , algolDeg , Aspect.CONJUNCTION , 5))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + FixedStar.ALGOL + " 形成 " + Aspect.CONJUNCTION);
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet, FixedStar.ALGOL, Aspect.CONJUNCTION}));
    }
    return Optional.empty();
  }

}
