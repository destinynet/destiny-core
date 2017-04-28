/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.jooq.lambda.tuple.Tuple.tuple;

public abstract class TransFourAbstractImpl implements ITransFour {

  protected abstract Map<Tuple2<Stem, Value>, ZStar> getTransMap();

  @Override
  public ZStar getStarOf(Stem stem, Value value) {
    return getTransMap().get(tuple(stem, value));
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
