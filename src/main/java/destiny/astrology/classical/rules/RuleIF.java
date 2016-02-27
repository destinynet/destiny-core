/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:09:26
 */
package destiny.astrology.classical.rules;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public interface RuleIF extends Predicate<Pair<Planet, HoroscopeContext>> {

  @Override
  default boolean test(Pair<Planet, HoroscopeContext> t) {
    return isApplicable(t.getLeft() , t.getRight());
  }

  boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext);

  String getName();

  String getName(Locale locale);

  Optional<String> getComment();

  Optional<String> getComment(Locale locale);
}
