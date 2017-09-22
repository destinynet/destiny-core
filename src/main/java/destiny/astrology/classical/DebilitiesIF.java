/**
 * @author smallufo
 * Created on 2007/12/20 at 下午 6:33:48
 */
package destiny.astrology.classical;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * 取得某行星 Planet 的 Debilities 衰弱程度 <BR>
 * 內定實作是 DebilitiesDefaultImpl
 */
public interface DebilitiesIF {

  @NotNull
  List<String> getComments(Planet planet, Horoscope h, Locale locale);

  @NotNull
  List<RuleIF> getRules();
}
