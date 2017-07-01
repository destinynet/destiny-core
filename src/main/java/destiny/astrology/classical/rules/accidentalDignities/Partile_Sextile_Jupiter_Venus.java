/**
 * @author smallufo 
 * Created on 2007/12/29 at 下午 11:56:41
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile sextile Jupiter or Venus. */
public final class Partile_Sextile_Jupiter_Venus extends Rule
{
  public Partile_Sextile_Jupiter_Venus()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLng();
    double jupiterDeg = horoscopeContext.getPosition(Planet.JUPITER).getLng();
    double venusDeg   = horoscopeContext.getPosition(Planet.VENUS).getLng();
    
    if (planet != Planet.JUPITER && AspectEffectiveModern.isEffective( planetDegree , jupiterDeg , Aspect.SEXTILE , 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.JUPITER + " 形成 " + Aspect.SEXTILE);
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.JUPITER, Aspect.SEXTILE}));
    }
    else if (planet != Planet.VENUS && AspectEffectiveModern.isEffective( planetDegree , venusDeg , Aspect.SEXTILE , 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.VENUS + " 形成 " + Aspect.SEXTILE);
      return Optional.of(Tuple.tuple("comment" , new Object[] {planet , Planet.VENUS , Aspect.SEXTILE}));
    }
    return Optional.empty();
  }

}
