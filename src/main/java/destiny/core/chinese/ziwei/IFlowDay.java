/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Branch;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 計算流日命宮
 * 兩種實作：
 *
 * 「流日地支」         {@link FlowDayBranchImpl}
 *
 * 「流月命宮，順數日」   {@link FlowDayFlowMonthMainHouseDepImpl}
 *
 */
public interface IFlowDay extends Descriptive {

  /**
   * @param flowMonthMainHouse 流月命宮
   */
  Branch getFlowDay(Branch flowDayBranch , int flowDayNum , Branch flowMonthMainHouse);

  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IFlowDay.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
