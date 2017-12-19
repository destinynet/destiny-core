/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:09:26
 */
package destiny.astrology.classical.rules;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Locale;
import java.util.function.Predicate;

public interface IRule extends Predicate<Tuple2<Planet, Horoscope>> {

  @Override
  default boolean test(Tuple2<Planet, Horoscope> t) {
    return isApplicable(t.v1() , t.v2());
  }

  boolean isApplicable(Planet planet, Horoscope h);

  String getName();

  String getName(Locale locale);

  /** 取得某 Locale 之下的註解 */
  @Nullable
  String getComment(Planet planet , Horoscope h , @NotNull Locale locale);

  @Nullable
  default String getComment(Planet planet , Horoscope h) {
    return getComment(planet , h , Locale.getDefault());
  }
}
