/**
 * @author smallufo
 * Created on 2008/1/1 at 上午 8:14:33
 */
package destiny.astrology.classical;

import destiny.astrology.IHoroscopeModel;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.IRule;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合 essentialDignities , accidentalDignities , debilitiesBean 
 * 列出所有 Rules
 */
public class RulesBean implements Serializable {

  private IEssentialDignities essentialDignitiesImpl;

  private IAccidentalDignities accidentalDignitiesImpl;

  private IDebilities debilitiesBean;

//  protected RulesBean() {
//  }

  public RulesBean(IEssentialDignities essentialDignitiesImpl, IAccidentalDignities accidentalDignitiesImpl, IDebilities debilitiesBean) {
    this.essentialDignitiesImpl = essentialDignitiesImpl;
    this.accidentalDignitiesImpl = accidentalDignitiesImpl;
    this.debilitiesBean = debilitiesBean;
  }

  @NotNull
  public List<String> getComments(Planet planet , IHoroscopeModel h , Locale locale) {
    return Stream.of(
      essentialDignitiesImpl.getComments(planet , h , locale).stream() ,
      accidentalDignitiesImpl.getComments(planet , h , locale).stream(),
      debilitiesBean.getComments(planet , h , locale).stream()
    ).flatMap(x -> x)
      .collect(Collectors.toList());
  }

  @NotNull
  public List<Pair<IRule, String>> getRuleAndComments(Planet planet , IHoroscopeModel h , Locale locale) {

    return Stream.of(
      essentialDignitiesImpl.getRules().stream() ,
      accidentalDignitiesImpl.getRules().stream() ,
      debilitiesBean.getRules().stream()
    ).flatMap(x -> x)
      .map(rule -> new Pair<>(rule , rule.getComment(planet , h , locale)))
      .filter(t2 -> t2.getSecond() != null)
      .map(t2 -> new Pair<>(t2.getFirst() , t2.getSecond()))
      .collect(Collectors.toList());
  }
}
