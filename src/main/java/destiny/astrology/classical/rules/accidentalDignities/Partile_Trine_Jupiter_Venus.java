/**
 * @author smallufo 
 * Created on 2007/12/29 at 下午 11:50:48
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Partile trine Jupiter or Venus. */
public final class Partile_Trine_Jupiter_Venus extends Rule
{
  public Partile_Trine_Jupiter_Venus()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double jupiterDeg = horoscopeContext.getPosition(Planet.JUPITER).getLongitude();
    double venusDeg   = horoscopeContext.getPosition(Planet.VENUS).getLongitude();
    
    if (planet != Planet.JUPITER && AspectEffectiveModern.isEffective( planetDegree , jupiterDeg , Aspect.TRINE , 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.JUPITER + " 形成 " + Aspect.TRINE);
      return new Tuple<>("comment" , new Object[] {planet , Planet.JUPITER , Aspect.TRINE});
    }
    else if (planet != Planet.VENUS && AspectEffectiveModern.isEffective( planetDegree , venusDeg , Aspect.TRINE , 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.VENUS + " 形成 " + Aspect.TRINE);
      return new Tuple<>("comment" , new Object[] {planet , Planet.VENUS , Aspect.TRINE});
    }
    
    return null;
  }

}
