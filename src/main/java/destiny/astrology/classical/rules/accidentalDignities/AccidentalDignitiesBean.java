/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 4:22:13
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import destiny.astrology.beans.BesiegedBean;
import destiny.astrology.classical.*;
import destiny.astrology.classical.rules.RuleIF;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccidentalDignitiesBean implements AccidentalDignitiesIF , Serializable
{
  /** 計算兩星體呈現某交角的時間 , 內定採用 SwissEph 的實作 */
  @Inject
  private RelativeTransitIF relativeTransitImpl;// = new RelativeTransitImpl();
  
  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作 */
  @Inject
  private DayNightDifferentiator dayNightImpl;// = new DayNightDifferentiatorImpl();

  @Inject
  private AspectEffectiveClassical aspectEffectiveClassical;// = new AspectEffectiveClassical();
  
  /** 判斷入相位或是出相位 */
  @Inject
  private AspectApplySeparateIF aspectApplySeparateImpl;// = new AspectApplySeparateImpl(aspectEffectiveClassical);

  @Inject
  private BesiegedBean besiegedBean;

  @Inject
  private TranslationOfLightIF translationOfLightImpl;

  @Inject
  private CollectionOfLightIF collectionOfLightImpl;

  private final RefranationIF refranationImpl;

  public AccidentalDignitiesBean(RefranationIF refranationImpl) {
    this.refranationImpl = refranationImpl;
  }

  private List<Applicable> rules = new ArrayList<Applicable>();

  @PostConstruct
  public void init() {
    this.rules = getDefaultRules();
  }
  
  @Override
  public List<RuleIF> getAccidentalDignities(Planet planet, HoroscopeContext horoscopeContext)
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
    list.add(new House_1_10());
    list.add(new House_4_7_11());
    list.add(new House_2_5());
    list.add(new House_9());
    list.add(new House_3());
    list.add(new Direct());
    list.add(new Swift());
    list.add(new Oriental());
    list.add(new Occidental());
    list.add(new Moon_Increase_Light());
    list.add(new Free_Combustion());
    list.add(new Cazimi());
    list.add(new Partile_Conj_Jupiter_Venus());
    list.add(new Partile_Conj_North_Node());
    list.add(new Partile_Trine_Jupiter_Venus());
    list.add(new Partile_Sextile_Jupiter_Venus());
    list.add(new Partile_Conj_Regulus());
    list.add(new Partile_Conj_Spica());
    list.add(new JoyHouse());
    list.add(new Hayz(dayNightImpl));
    list.add(new Besieged_Jupiter_Venus(besiegedBean));
    list.add(new Translation_of_Light( translationOfLightImpl ));
    list.add(new Collection_of_Light(collectionOfLightImpl));
    list.add(new Refrain_from_Mars_Saturn(refranationImpl));
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

  public void setRelativeTransitImpl(RelativeTransitIF relativeTransitImpl)
  {
    this.relativeTransitImpl = relativeTransitImpl;
  }

  public void setDayNightImpl(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }


}
