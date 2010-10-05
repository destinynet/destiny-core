/**
 * @author smallufo 
 * Created on 2008/1/17 at 上午 1:35:01
 */ 
package destiny.core.calendar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class LocationDecorator
{
  private final static Map<Locale , Decorator<Location>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Location>>());
  static
  {
    implMap.put(Locale.TAIWAN , new LocationDecoratorTaiwan());
    implMap.put(Locale.CHINA  , new LocationDecoratorChina());
    implMap.put(Locale.ENGLISH, new LocationDecoratorEnglish());
  }
  
  public static String getOutputString(Location location , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(location);
  }
  
}
