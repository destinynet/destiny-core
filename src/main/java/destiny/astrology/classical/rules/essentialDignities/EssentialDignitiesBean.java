/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 4:56:19
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.EssentialDignitiesIF;
import destiny.astrology.classical.rules.RuleIF;

public class EssentialDignitiesBean implements EssentialDignitiesIF , Serializable
{
  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作 */
  private DayNightDifferentiator dayNightImpl;// = new DayNightDifferentiatorImpl();
  
  private List<Applicable> rules = new ArrayList<Applicable>();
  
  public EssentialDignitiesBean(DayNightDifferentiator dayNightImpl)
  {
    System.err.println("EssentialDignitiesBean init");
    this.dayNightImpl = dayNightImpl;
    rules = getDefaultRules();
  }
  
  @Override
  public List<RuleIF> getEssentialDignities(Planet planet, HoroscopeContext horoscopeContext)
  {
    List<RuleIF> resultList = new ArrayList<RuleIF>();
    
    for(Applicable each : rules)
    {
      if(each.isApplicable(planet, horoscopeContext))
        resultList.add(each);
    }
    return resultList;
  }
  
  
  /** 內定的 Rules */
  private List<Applicable> getDefaultRules()
  {
    List<Applicable> list = new ArrayList<Applicable>();
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
