/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:56:28
 */ 
package destiny.core.calendar.decorators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class MinuteDecorator
{
  private static Map<Locale , Decorator<Integer>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Integer>>());
  static
  {
    implMap.put(Locale.CHINESE , new MinuteDecoratorChinese());
    implMap.put(Locale.ENGLISH , new MinuteDecoratorEnglish());
  }
  
  public static String getOutputString(int value ,  Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(value); 
  }
}
