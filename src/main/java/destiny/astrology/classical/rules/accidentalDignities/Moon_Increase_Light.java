/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:22:44
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Horoscope2;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Moon increasing in light (月增光/上弦月) , or occidental of the Sun. */
public final class Moon_Increase_Light extends Rule
{
  public Moon_Increase_Light()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLng();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLng();
    
    if (planet == Planet.MOON)
    {
      if ( Horoscope2.isOccidental(planetDegree , sunDegree))
      {
        // addComment(Locale.TAIWAN , planet + " 在太陽西邊（月增光/上弦月）");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
