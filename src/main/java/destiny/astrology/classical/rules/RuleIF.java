/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:09:26
 */
package destiny.astrology.classical.rules;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public interface RuleIF extends Predicate<Tuple2<Planet, HoroscopeContextIF>> {

  @Override
  default boolean test(Tuple2<Planet, HoroscopeContextIF> t) {
    return isApplicable(t.v1() , t.v2());
  }

  boolean isApplicable(Planet planet, HoroscopeContextIF horoscopeContext);

  String getName();

  String getName(Locale locale);

  Optional<String> getComment();

  Optional<String> getComment(Locale locale);
}
