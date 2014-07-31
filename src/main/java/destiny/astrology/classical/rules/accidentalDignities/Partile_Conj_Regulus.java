/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:00:27
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.FixedStar;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000. */
public final class Partile_Conj_Regulus extends Rule
{
  public Partile_Conj_Regulus()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double regulusDeg = horoscopeContext.getPosition(FixedStar.REGULUS).getLongitude();
    
    if (AspectEffectiveModern.isEffective(planetDegree , regulusDeg , Aspect.CONJUNCTION , 1))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + FixedStar.REGULUS + " 形成 " + Aspect.CONJUNCTION);
      return new Tuple<>("comment" , new Object[] {planet , FixedStar.REGULUS , Aspect.CONJUNCTION});
    }
    return null;
  }

}
