/**
 * @author smallufo 
 * Created on 2008/1/1 at 上午 8:14:33
 */ 
package destiny.astrology.classical;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合 essentialDignities , accidentalDignities , debilitiesBean 
 * 列出所有 Rules
 */
public class RulesBean implements Serializable
{
  private EssentialDignitiesIF essentialDignitiesImpl;
  private AccidentalDignitiesIF accidentalDignitiesImpl;
  private DebilitiesIF debilitiesBean;

  protected RulesBean() {
  }

  public RulesBean(EssentialDignitiesIF essentialDignitiesImpl , AccidentalDignitiesIF accidentalDignitiesImpl , DebilitiesIF debilitiesBean) {
    this.essentialDignitiesImpl = essentialDignitiesImpl;
    this.accidentalDignitiesImpl = accidentalDignitiesImpl;
    this.debilitiesBean = debilitiesBean;
  }
  
  @NotNull
  public List<RuleIF> getRules(Planet planet, HoroscopeContext horoscopeContext) {

    return Stream.of(
      essentialDignitiesImpl.getEssentialDignities(planet, horoscopeContext).stream(),
      accidentalDignitiesImpl.getAccidentalDignities(planet, horoscopeContext).stream(),
      debilitiesBean.getDebilities(planet, horoscopeContext).stream()
    )
      //.reduce(Stream.empty(), Stream::concat)
      .flatMap(x -> x)
      .collect(Collectors.toList());
  }
}
