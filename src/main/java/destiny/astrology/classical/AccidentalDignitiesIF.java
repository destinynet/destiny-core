/**
 * @author smallufo
 * Created on 2007/12/13 at 下午 4:58:47
 */
package destiny.astrology.classical;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * 取得某行星 Planet 的 Accidental Dignities 強度
 */
public interface AccidentalDignitiesIF {

  @NotNull
  List<String> getComments(Planet planet, Horoscope h, Locale locale);

  @NotNull
  List<RuleIF> getRules();
}
