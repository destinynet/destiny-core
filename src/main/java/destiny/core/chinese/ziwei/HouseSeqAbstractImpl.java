/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.tools.ArrayTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class HouseSeqAbstractImpl implements IHouseSeq , Serializable {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public House next(House from, int n) {
    return get(getIndex(from) + n);
  }

  @Override
  public int getAheadOf(House h1, House h2) {
    int index1 = getIndex(h1);
    int index2 = getIndex(h2);
    logger.trace("index1({}) = {} , index2({}) = {}" , h1 , index1 , h2 , index2);
    if (index1 < 0 || index2 < 0)
      return -1;
    int steps = index1 - index2;
    return (steps >=0 ? steps : steps + 12);
  }

  @Override
  public String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(HouseSeqAbstractImpl.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  public String getDescription(Locale locale) {
    return getTitle(locale);
  }

  private House get(int index) {
    return ArrayTools.get(getHouses() , index);
  }

  private int getIndex(House h) {
    return Arrays.asList(getHouses()).indexOf(h);
  }
}
