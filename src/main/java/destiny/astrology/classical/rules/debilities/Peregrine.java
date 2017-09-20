/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 4:53:40
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import destiny.astrology.classical.Dignity;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Peregrine. */
public final class Peregrine extends EssentialRule {
  /** 計算白天黑夜的實作 */
  private final DayNightDifferentiator dayNightImpl;
  
  public Peregrine(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    double planetDegree = h.getPosition(planet).getLng();
    //取得此 Planet 在什麼星座
    ZodiacSign sign = h.getZodiacSign(planet);
    
    DayNight dayNight = dayNightImpl.getDayNight(h.getLmt(), h.getLocation());
    if (planet != essentialImpl.getPoint(sign, Dignity.RULER) &&
        planet != essentialImpl.getPoint(sign, Dignity.EXALTATION) &&
        planet != essentialImpl.getPoint(sign, Dignity.DETRIMENT) &&
        planet != essentialImpl.getPoint(sign, Dignity.FALL) &&
        planet != essentialImpl.getTermsPoint(sign, planetDegree) &&
        planet != essentialImpl.getFacePoint(planetDegree)
        ) {
      //判定日夜 Triplicity
      if (!(dayNight == DayNight.DAY && planet == essentialImpl.getTriplicityPoint(sign, DayNight.DAY)) && !(dayNight == DayNight.NIGHT && planet == essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT))) {
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
