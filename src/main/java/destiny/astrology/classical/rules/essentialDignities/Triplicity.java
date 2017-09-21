/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:19:56
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities). */
public final class Triplicity extends Rule
{
  /** 計算白天黑夜的實作 */
  private final DayNightDifferentiator dayNightImpl;
  
  public Triplicity(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    return h.getZodiacSign(planet).flatMap(sign -> {
      DayNight dayNight = dayNightImpl.getDayNight(h.getLmt(), h.getLocation());
      if(  (dayNight == DayNight.DAY   && planet == essentialImpl.getTriplicityPoint(sign, DayNight.DAY )) ||
           (dayNight == DayNight.NIGHT && planet == essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT))   )
      {
        //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 "+dayNight.toString()+" 之 Triplicity");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, sign, dayNight}));
      }
      return Optional.empty();
    });
  }
}
