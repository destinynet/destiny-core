/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.Gender;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** 起大限 */
public interface IBigRange extends Descriptive {

  Tuple2<Double , Double> getRange(House house, int set, YinYangIF yinYang, Gender gender, FortuneOutput fortuneOutput, IHouseSeq houseSeq);


  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IBigRange.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
