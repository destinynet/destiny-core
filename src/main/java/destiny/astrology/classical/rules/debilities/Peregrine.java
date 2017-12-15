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

  @NotNull
  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h) {
    return h.getPositionOpt(planet).flatMap(pos -> {
      double planetDegree = pos.getLng();
      //取得此 Planet 在什麼星座
      return h.getZodiacSignOpt(planet).flatMap(sign -> {
        DayNight dayNight = dayNightImpl.getDayNight(h.getLmt(), h.getLocation());
        if (planet != getEssentialImpl().getPoint(sign, Dignity.RULER).orElse(null) &&
            planet != getEssentialImpl().getPoint(sign, Dignity.EXALTATION).orElse(null) &&
            planet != getEssentialImpl().getPoint(sign, Dignity.DETRIMENT).orElse(null) &&
            planet != getEssentialImpl().getPoint(sign, Dignity.FALL).orElse(null) &&
            planet != getEssentialImpl().getTermsPoint(sign, planetDegree) &&
            planet != getEssentialImpl().getFacePoint(planetDegree)
            ) {
          //判定日夜 Triplicity
          if (   !(dayNight == DayNight.DAY && planet == getEssentialImpl().getTriplicityPoint(sign, DayNight.DAY))
              && !(dayNight == DayNight.NIGHT && planet == getEssentialImpl().getTriplicityPoint(sign, DayNight.NIGHT))
            ) {
            return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
          }
        }
        return Optional.empty();
      });
    });

  }

}
