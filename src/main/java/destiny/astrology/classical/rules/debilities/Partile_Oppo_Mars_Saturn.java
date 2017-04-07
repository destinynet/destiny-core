/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:38:12
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveModern;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile opposite Mars or Saturn. */
public final class Partile_Oppo_Mars_Saturn extends Rule
{
  public Partile_Oppo_Mars_Saturn()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double marsDeg = horoscopeContext.getPosition(Planet.MARS).getLongitude();
    double saturnDeg = horoscopeContext.getPosition(Planet.SATURN).getLongitude();
    
    if ( planet != Planet.MARS && AspectEffectiveModern.isEffective( planetDegree , marsDeg , Aspect.OPPOSITION , 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.MARS + " 形成 " + Aspect.OPPOSITION);
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.MARS, Aspect.OPPOSITION}));
    }
    else if ( planet != Planet.SATURN && AspectEffectiveModern.isEffective( planetDegree , saturnDeg , Aspect.OPPOSITION, 1.0))
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.SATURN + " 形成 " + Aspect.OPPOSITION);
      return Optional.of(Tuple.tuple("comment" , new Object[]{planet , Planet.SATURN , Aspect.OPPOSITION}));
    }
    return Optional.empty();
  }

}
