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
 * 計算流月命宮
 * 三種實作：
 *
 * 「 流年斗君，順數月」 {@link FlowMonthDefaultImpl}
 * 「 流月地支」        {@link FlowMonthFixedImpl}
 * 「 流年命宮，順數月」 {@link FlowMonthYearMainHouseDepImpl}
 * */
public interface IFlowMonth extends Descriptive {

  /**
   * @param flowYear 流年
   * @param flowMonth 欲求算的流月
   * @param birthMonth 「出生」的生月
   * @param birthHour 「出生」的時辰
   */
  Branch getFlowMonth(Branch flowYear, Branch flowMonth, int birthMonth, Branch birthHour);

  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IFlowMonth.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
