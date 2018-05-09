/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:09:26
 */
package destiny.astrology.classical.rules;

import destiny.astrology.IHoroscopeModel;
import destiny.astrology.Planet;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Predicate;

public interface IRule extends Predicate<Pair<Planet, IHoroscopeModel>> {

  @Override
  default boolean test(Pair<Planet, IHoroscopeModel> t) {
    return isApplicable(t.getFirst() , t.getSecond());
  }

  boolean isApplicable(Planet planet, IHoroscopeModel h);

  String getName();

  String getName(Locale locale);

  /** 取得某 Locale 之下的註解 */
  @Nullable
  String getComment(Planet planet , IHoroscopeModel h , @NotNull Locale locale);

  @Nullable
  default String getComment(Planet planet , IHoroscopeModel h) {
    return getComment(planet , h , Locale.getDefault());
  }
}
