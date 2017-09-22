/**
 * @author smallufo 
 * Created on 2007/12/31 at 下午 10:42:30
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import destiny.astrology.classical.DebilitiesIF;
import destiny.astrology.classical.RefranationIF;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class DebilitiesBean implements DebilitiesIF , Serializable {

  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作 */
  @Inject
  private DayNightDifferentiator dayNightImpl;

  @Inject
  private BesiegedIF besiegedImpl;

  @Inject
  private RefranationIF refranationImpl;

  private List<RuleIF> rules = new ArrayList<>();

  public DebilitiesBean()
  {
  }

  @PostConstruct
  public void init() {
    this.rules = getDefaultRules();
  }

  @NotNull
  @Override
  public List<String> getComments(Planet planet, Horoscope h, Locale locale) {
    return rules.stream()
      .map(ruleIF ->  ruleIF.getComment(planet , h , locale))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
  }


  @NotNull
  private List<RuleIF> getDefaultRules() {
    List<RuleIF> list = new ArrayList<>();
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
    list.add(new Besieged_Mars_Saturn(besiegedImpl));
    list.add(new Partile_Oppo_Mars_Saturn());
    list.add(new Partile_Square_Mars_Saturn());
    list.add(new Conj_Algol());
    list.add(new Out_of_Sect(dayNightImpl));
    list.add(new MutualDeception(dayNightImpl));
    list.add(new Refrain_from_Venus_Jupiter(refranationImpl));
    return list;
  }

  @Override
  @NotNull
  public List<RuleIF> getRules() {
    return rules;
  }

}
