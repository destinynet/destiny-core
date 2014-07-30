/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:13:56
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Mars, Jupiter, or Saturn occidental to the Sun. */
public final class Occidental extends Rule
{
  public Occidental()
  {
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.MARS || planet == Planet.JUPITER || planet == Planet.SATURN)
    {
      double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
      double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
      if (Horoscope.isOccidental(planetDegree , sunDegree))
      {
        //addComment(Locale.TAIWAN , planet + " 在太陽西邊");
        return new Tuple<String , Object[]>("comment" , new Object[]{planet});
      }  
    }
    return null;
  }

}
