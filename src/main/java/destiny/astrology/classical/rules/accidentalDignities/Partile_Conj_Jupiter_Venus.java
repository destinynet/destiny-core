/**
 * @author smallufo 
 * Created on 2007/12/29 at 下午 11:42:03
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile conjunction with Jupiter or Venus. 
 * 和金星或木星合相，交角 1 度內 */
public final class Partile_Conj_Jupiter_Venus extends Rule
{
  public Partile_Conj_Jupiter_Venus()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h)
  {
    double planetDegree = h.getPosition(planet).getLng();
    double jupiterDeg = h.getPosition(Planet.JUPITER).getLng();
    double venusDeg   = h.getPosition(Planet.VENUS).getLng();
    
    if (planet != Planet.JUPITER && Horoscope.getAngle(planetDegree , jupiterDeg) <= 1)
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.JUPITER + " 形成 " + Aspect.CONJUNCTION);
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.JUPITER, Aspect.CONJUNCTION}));
    }
    else if (planet != Planet.VENUS && Horoscope.getAngle(planetDegree , venusDeg) <= 1)
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.VENUS + " 形成 " + Aspect.CONJUNCTION);
      return Optional.of(Tuple.tuple("comment" , new Object[] {planet , Planet.VENUS , Aspect.CONJUNCTION}));
    }
    return Optional.empty();
  }

}
