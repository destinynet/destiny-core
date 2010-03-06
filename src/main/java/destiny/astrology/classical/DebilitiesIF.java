/**
 * @author smallufo 
 * Created on 2007/12/20 at 下午 6:33:48
 */ 
package destiny.astrology.classical;

import java.util.List;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;

/**
 * 取得某行星 Planet 的 Debilities 衰弱程度 <BR>
 * 內定實作是 DebilitiesDefaultImpl
 */
public interface DebilitiesIF
{
  public List<RuleIF> getDebilities(Planet planet , HoroscopeContext horoscopeContext);
}
