/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:42:01
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Partile square Mars or Saturn. */
public final class Partile_Square_Mars_Saturn extends Rule
{
  public Partile_Square_Mars_Saturn()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double marsDeg = horoscopeContext.getPosition(Planet.MARS).getLongitude();
    double saturnDeg = horoscopeContext.getPosition(Planet.SATURN).getLongitude();
    
    if ( planet != Planet.MARS && AspectEffectiveModern.isEffective( planetDegree , marsDeg , Aspect.SQUARE , 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.MARS + " 形成 " + Aspect.SQUARE);
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet, Planet.MARS, Aspect.SQUARE}));
    }
    else if ( planet != Planet.SATURN && AspectEffectiveModern.isEffective( planetDegree , saturnDeg , Aspect.SQUARE, 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.SATURN + " 形成 " + Aspect.SQUARE);
      return Optional.of(ImmutablePair.of("comment" , new Object[]{planet , Planet.SATURN , Aspect.SQUARE}));
    }
    return Optional.empty();
  }

}
