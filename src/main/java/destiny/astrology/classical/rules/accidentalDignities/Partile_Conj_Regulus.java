/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:00:27
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000. */
public final class Partile_Conj_Regulus extends Rule
{
  public Partile_Conj_Regulus()
  {
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double regulusDeg = horoscopeContext.getPosition(FixedStar.REGULUS).getLongitude();
    
    if (AspectEffectiveModern.isEffective(planetDegree , regulusDeg , Aspect.CONJUNCTION , 1))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + FixedStar.REGULUS + " 形成 " + Aspect.CONJUNCTION);
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet, FixedStar.REGULUS, Aspect.CONJUNCTION}));
    }
    return Optional.empty();
  }

}
