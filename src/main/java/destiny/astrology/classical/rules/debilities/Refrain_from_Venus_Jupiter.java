/**
 * Created by smallufo at 2008/11/11 下午 10:09:47
 */
package destiny.astrology.classical.rules.debilities;

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

public class Refrain_from_Venus_Jupiter extends Rule {

  private final RefranationIF refranationImpl;

  public Refrain_from_Venus_Jupiter(RefranationIF refranationImpl) {
    this.refranationImpl = refranationImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, Horoscope h) {
    //太陽 / 月亮不會逆行
    if (planet == Planet.MOON || planet == Planet.SUN)
      return Optional.empty();

    Point otherPoint;

    if (planet != Planet.VENUS) {
      otherPoint = Planet.VENUS;

      Optional<Pair<Point, Aspect>> result = refranationImpl.getResult(h , planet , otherPoint);
      if (result.isPresent()) {
        Aspect aspect = result.get().getSecond();
        logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)" , planet , otherPoint , aspect);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, aspect}));
      }

//      Tuple3<Boolean, Point, Aspect> t = refranationImpl.resultOf(h, planet, otherPoint);
//      if (t.v1()) {
//        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, t.v3()}));
//      }
    }

    if ( planet != Planet.JUPITER) {
      otherPoint = Planet.JUPITER;

      Optional<Pair<Point, Aspect>> result = refranationImpl.getResult(h , planet , otherPoint);
      if (result.isPresent()) {
        Aspect aspect = result.get().getSecond();
        logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)" , planet , otherPoint , aspect);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, aspect}));
      }

//      Tuple3<Boolean, Point, Aspect> t = refranationImpl.resultOf(h, planet, otherPoint);
//      if (t.v1) {
//        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, t.v3()}));
//      }
    }
    return Optional.empty();
  }

}
