/**
 * Created by smallufo at 2008/11/11 下午 8:29:36
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.classical.RefranationIF;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/**
 * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
 */
public final class Refrain_from_Mars_Saturn extends Rule {

  private final RefranationIF refranationImpl;

  public Refrain_from_Mars_Saturn(RefranationIF refranationImpl) {
    this.refranationImpl = refranationImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, Horoscope h) {
    // 太陽 / 月亮不會逆行
    if (planet == Planet.MOON || planet == Planet.SUN)
      return Optional.empty();

    Point otherPoint;

    if (planet != Planet.MARS) {
      otherPoint = Planet.MARS;

      Optional<Pair<Point, Aspect>> result = refranationImpl.getResult(h , planet , otherPoint);
      if (result.isPresent()) {
        Aspect aspect = result.get().getSecond();
        logger.debug("{} 逃過了與 {} 形成 {} (Refranation)" , planet , otherPoint , aspect);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, aspect}));
      }
    }

    if (planet != Planet.SATURN) {
      otherPoint = Planet.SATURN;

      Optional<Pair<Point, Aspect>> result = refranationImpl.getResult(h, planet, otherPoint);
      if (result.isPresent()) {
        Aspect aspect = result.get().getSecond();
        logger.debug("{} 逃過了與 {} 形成 {} (Refranation)" , planet , otherPoint , aspect);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, aspect}));
      }
    }
    return Optional.empty();
  }

}
