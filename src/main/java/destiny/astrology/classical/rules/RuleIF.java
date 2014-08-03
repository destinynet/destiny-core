/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:09:26
 */ 
package destiny.astrology.classical.rules;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

import java.util.Locale;
import java.util.Optional;

public interface RuleIF
{
  public boolean isApplicable(Planet planet , HoroscopeContext horoscopeContext);
  public String getName();
  public String getName(Locale locale);

  public Optional<String> getComment();

  public Optional<String> getComment(Locale locale);
}
