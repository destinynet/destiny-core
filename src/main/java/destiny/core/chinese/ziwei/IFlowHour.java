/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Branch;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** 流時命宮 */
public interface IFlowHour extends Descriptive {

  /**
   * @param hour  欲求算的當日時辰
   * @param flowDayMainHour 當日命宮
   */
  Branch getFlowHour(Branch hour , Branch flowDayMainHour);


  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IFlowHour.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
