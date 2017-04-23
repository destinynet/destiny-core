/**
 * Created by smallufo on 2017-04-17.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** 「流運」的類型 */
public enum FlowType implements Descriptive {
  本命, 大限, 流年, 流月, 流日, 流時;

  @Override
  public String getTitle(Locale locale) {
    try {
        return ResourceBundle.getBundle(FlowType.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
  }

  @Override
  public String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
