/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 4:56:19
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.EssentialDignitiesIF;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EssentialDignitiesBean implements EssentialDignitiesIF , Serializable
{
  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作 */
  private DayNightDifferentiator dayNightImpl;
  
  private List<Applicable> rules = new ArrayList<>();
  
  public EssentialDignitiesBean(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
    rules = getDefaultRules();
  }
  
  @NotNull
  @Override
  public List<RuleIF> getEssentialDignities(Planet planet, HoroscopeContext horoscopeContext)
  {
    return rules.stream().filter(each -> each.isApplicable(planet, horoscopeContext)).collect(Collectors.toList());
  }
  
  
  /** 內定的 Rules */
  @NotNull
  private List<Applicable> getDefaultRules()
  {
    List<Applicable> list = new ArrayList<>();
    list.add(new Ruler(dayNightImpl));
    list.add(new Exaltation(dayNightImpl));
    list.add(new MixedReception(dayNightImpl));
    list.add(new Triplicity(dayNightImpl));
    list.add(new Term());
    list.add(new Face());
    return list;
  }

  public List<Applicable> getRules()
  {
    return rules;
  }

  public void setRules(List<Applicable> rules)
  {
    this.rules = rules;
  }

  public void setDayNightImpl(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

}
