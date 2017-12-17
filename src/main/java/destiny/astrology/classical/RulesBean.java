/**
 * @author smallufo
 * Created on 2008/1/1 at 上午 8:14:33
 */
package destiny.astrology.classical;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.rules.RuleIF;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

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

  protected RulesBean() {
  }

  public RulesBean(IEssentialDignities essentialDignitiesImpl, IAccidentalDignities accidentalDignitiesImpl, IDebilities debilitiesBean) {
    this.essentialDignitiesImpl = essentialDignitiesImpl;
    this.accidentalDignitiesImpl = accidentalDignitiesImpl;
    this.debilitiesBean = debilitiesBean;
  }

  @NotNull
  public List<String> getComments(Planet planet , Horoscope h , Locale locale) {
    return Stream.of(
      essentialDignitiesImpl.getComments(planet , h , locale).stream() ,
      accidentalDignitiesImpl.getComments(planet , h , locale).stream(),
      debilitiesBean.getComments(planet , h , locale).stream()
    ).flatMap(x -> x)
      .collect(Collectors.toList());
  }

  @NotNull
  public List<Tuple2<RuleIF , String>> getRuleAndComments(Planet planet , Horoscope h , Locale locale) {
    return Stream.of(
      essentialDignitiesImpl.getRules().stream() ,
      accidentalDignitiesImpl.getRules().stream() ,
      debilitiesBean.getRules().stream()
    ).flatMap(x -> x)
      .map(rule -> Tuple.tuple(rule , rule.getCommentOpt(planet , h , locale)))
      .filter(t2 -> t2.v2().isPresent())
      .map(t2 -> Tuple.tuple(t2.v1() , t2.v2().get()))
      .collect(Collectors.toList());
  }
}
