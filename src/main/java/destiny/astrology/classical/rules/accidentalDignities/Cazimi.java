/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:26:11
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Cazimi (within 17 minutes of the Sun). */
public final class Cazimi extends Rule
{
  public Cazimi()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) < 17.0/60 )
      {
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
