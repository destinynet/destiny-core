/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:28:28
 */ 
package destiny.core.calendar.decorators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class DayDecorator
{
  private static Map<Locale , Decorator<Integer>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Integer>>());
  static
  {
    implMap.put(Locale.CHINESE , new DayDecoratorChinese());
    implMap.put(Locale.ENGLISH , new DayDecoratorEnglish());
  }
  
  public static String getOutputString(int value ,  Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(value); 
  }
}
