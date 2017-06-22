/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** 取命宮地支 */
public interface IMainHouse extends Descriptive {

  Branch getMainHouse(LocalDateTime lmt , Location loc);

  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IMainHouse.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
