/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese;

import destiny.core.Descriptive;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 輸出大運的模式
 */
public enum FortuneOutput implements Descriptive {
  虛歲,
  西元,
  民國,
  實歲;

  @Override
  public String getTitle(Locale locale) {
    try {
        return ResourceBundle.getBundle(FortuneOutput.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
  }

  @Override
  public String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
