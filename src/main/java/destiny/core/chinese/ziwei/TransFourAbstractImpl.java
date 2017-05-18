/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.Table;
import destiny.core.chinese.Stem;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class TransFourAbstractImpl implements ITransFour , Serializable {

  protected abstract Table<Stem, Value, ZStar> getTable();

  @Override
  public ZStar getStarOf(Stem stem, Value value) {
    return getTable().get(stem , value);
  }

  @Override
  public String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(TransFourAbstractImpl.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }
}
