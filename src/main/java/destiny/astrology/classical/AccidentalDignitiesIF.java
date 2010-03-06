/**
 * @author smallufo 
 * Created on 2007/12/13 at 下午 4:58:47
 */ 
package destiny.astrology.classical;

import java.util.List;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;

/**
 * 取得某行星 Planet 的 Accidental Dignities 強度 <BR>
 * 內定實作是 AccidentalDignityDefaultImpl
 */
public interface AccidentalDignitiesIF
{
  public List<RuleIF> getAccidentalDignities(Planet planet , HoroscopeContext horoscopeContext);
}
