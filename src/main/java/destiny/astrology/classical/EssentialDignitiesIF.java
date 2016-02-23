/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 2:27:21
 */
package destiny.astrology.classical;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 取得某行星 Planet 的 Essential Dignities 強度 <BR>
 * 內定實作是 EssentialDignityDefaultImpl
 */
public interface EssentialDignitiesIF {
  @NotNull
  List<RuleIF> getEssentialDignities(Planet planet, HoroscopeContext horoscopeContext);
}
