/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Branch;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** 流年排法(的命宮) : 目前只有兩個實作 : 「 流年地支」或「 流年斗君」。 */
public interface IFlowYear extends Descriptive {

  Branch getFlowYear(Branch flowYearBranch , int birthMonth , Branch birthHour);


  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IFlowYear.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
