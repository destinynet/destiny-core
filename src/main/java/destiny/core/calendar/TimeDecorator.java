/**
 * @author smallufo 
 * Created on 2008/1/16 at 下午 11:05:18
 */ 
package destiny.core.calendar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class TimeDecorator
{
  private static Map<Locale , Decorator<Time>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Time>>());
  static
  {
    implMap.put(Locale.TAIWAN , new TimeDecoratorChinese());
    implMap.put(Locale.ENGLISH , new TimeDecoratorEnglish());
    implMap.put(Locale.JAPAN, new TimeDecoratorJapanese());
  }
  
  public static String getOutputString(Time time , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(time);
  }
}
