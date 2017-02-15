/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:19:56
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities). */
public final class Triplicity extends Rule
{
  /** 計算白天黑夜的實作 */
  private DayNightDifferentiator dayNightImpl;
  
  public Triplicity(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);

    DayNight dayNight = dayNightImpl.getDayNight(horoscopeContext.getLmt().toLocalDateTime(), horoscopeContext.getLocation());
    if(  (dayNight == DayNight.DAY   && planet == essentialImpl.getTriplicityPoint(sign, DayNight.DAY )) ||  
         (dayNight == DayNight.NIGHT && planet == essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT))   ) 
    {
      //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 "+dayNight.toString()+" 之 Triplicity");
      return Optional.of(ImmutablePair.of("comment", new Object[]{planet, sign, dayNight}));
    }
    return Optional.empty();
  }
}
