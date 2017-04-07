/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:25:34
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Under the Sunbeams (between 8.5 and 17 from Sol). */
public final class Sunbeam extends Rule
{

  public Sunbeam()
  {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet != Planet.SUN)
    {
      if (horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) > 8.5 &&
          horoscopeContext.getHoroscope().getAngle(planet , Planet.SUN) <= 17)
      {
        //addComment(Locale.TAIWAN , planet + " 被太陽曬傷 (Sunbeam)");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
