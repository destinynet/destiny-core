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
import java.util.ArrayList;
import java.util.List;

/**
 * 集合 essentialDignities , accidentalDignities , debilitiesBean 
 * 列出所有 Rules
 */
public class RulesBean implements Serializable
{
  EssentialDignitiesIF essentialDignitiesImpl;
  AccidentalDignitiesIF accidentalDignitiesImpl;
  DebilitiesIF debilitiesBean;

  protected RulesBean() {
  }

  public RulesBean(EssentialDignitiesIF essentialDignitiesImpl , AccidentalDignitiesIF accidentalDignitiesImpl , DebilitiesIF debilitiesBean)
  {
    this.essentialDignitiesImpl = essentialDignitiesImpl;
    this.accidentalDignitiesImpl = accidentalDignitiesImpl;
    this.debilitiesBean = debilitiesBean;
  }
  
  @NotNull
  public List<RuleIF> getRules(Planet planet, HoroscopeContext horoscopeContext)
  {
    List<RuleIF> resultList = new ArrayList<>();
    List<RuleIF> essentialDignitiesRules = essentialDignitiesImpl.getEssentialDignities(planet, horoscopeContext);
    List<RuleIF> accidentalDignitiesRules = accidentalDignitiesImpl.getAccidentalDignities(planet, horoscopeContext);
    List<RuleIF> debilitiesRules = debilitiesBean.getDebilities(planet, horoscopeContext);
    
    resultList.addAll(essentialDignitiesRules);
    resultList.addAll(accidentalDignitiesRules);
    resultList.addAll(debilitiesRules);
    
    return resultList;
  }
}
