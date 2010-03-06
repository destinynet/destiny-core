/**
 * @author smallufo 
 * Created on 2007/12/31 at 下午 10:42:30
 */ 
package destiny.astrology.classical.rules.debilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.AspectApplySeparateImpl;
import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.RetrogradeIF;
import destiny.astrology.classical.AspectEffectiveClassical;
import destiny.astrology.classical.DebilitiesIF;
import destiny.astrology.classical.rules.RuleIF;

public class DebilitiesBean implements DebilitiesIF , Serializable
{
  /** 計算兩星交角的介面 , 內定採用 SwissEph 的實作 */
  private final RelativeTransitIF relativeTransitImpl;// = new RelativeTransitImpl();
  
  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作 */
  private final DayNightDifferentiator dayNightImpl;// = new DayNightDifferentiatorImpl();
  
  /** 判斷入相位，或是出相位 的實作 , 內定採用古典占星 */
  private AspectApplySeparateIF aspectApplySeparateImpl;// = new AspectApplySeparateImpl(new AspectEffectiveClassical());
  
  /** 計算星體逆行的介面，目前只支援 Planet , 目前的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！) */
  private final RetrogradeIF retrogradeImpl;// = new RetrogradeImpl();
  
  private List<Applicable> rules = new ArrayList<Applicable>();
  
  public DebilitiesBean(RelativeTransitIF relativeTransitImpl , DayNightDifferentiator dayNightImpl , RetrogradeIF retrogradeImpl , AspectEffectiveClassical aspectEffectiveClassical)
  {
    this.relativeTransitImpl = relativeTransitImpl;
    this.dayNightImpl = dayNightImpl;
    this.retrogradeImpl = retrogradeImpl;
    this.aspectApplySeparateImpl = new AspectApplySeparateImpl(aspectEffectiveClassical);
    this.rules = getDefaultRules();
  }
  
  @Override
  public List<RuleIF> getDebilities(Planet planet, HoroscopeContext horoscopeContext)
  {
    List<RuleIF> resultList = new ArrayList<RuleIF>();
    
    for(Applicable each : rules)
    {
      if(each.isApplicable(planet, horoscopeContext))
        resultList.add(each);
    }
    return resultList;
  }

  
  private List<Applicable> getDefaultRules()
  {
    List<Applicable> list = new ArrayList<Applicable>();
    list.add(new Detriment());
    list.add(new Fall());
    list.add(new Peregrine(dayNightImpl));
    list.add(new House_12());
    list.add(new House_6_8());
    list.add(new Retrograde());
    list.add(new Slower());
    list.add(new Occidental());
    list.add(new Oriental());
    list.add(new Moon_Decrease_Light());
    list.add(new Combustion());
    list.add(new Sunbeam());
    list.add(new Partile_Conj_Mars_Saturn());
    list.add(new Partile_Conj_South_Node());
    list.add(new Besieged_Mars_Saturn(relativeTransitImpl));
    list.add(new Partile_Oppo_Mars_Saturn());
    list.add(new Partile_Square_Mars_Saturn());
    list.add(new Conj_Algol());
    list.add(new Out_of_Sect(dayNightImpl));
    list.add(new MutualDeception(dayNightImpl));
    list.add(new Refranate_from_Venus_Jupiter(aspectApplySeparateImpl , relativeTransitImpl , retrogradeImpl ));
    return list;
  }
  
  public List<Applicable> getRules()
  {
    return this.rules;
  }
  
  public void setRules(List<Applicable> rules)
  {
    this.rules = rules;
  }

  
}
