/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 12:31:36
 */ 
package destiny.core.calendar.decorators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class YearDecorator
{
  private static Map<Locale , Decorator<Integer>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Integer>>());
  static
  {
    implMap.put(Locale.TAIWAN  , new YearDecoratorTaiwan());
    implMap.put(Locale.CHINESE , new YearDecoratorChinese());
    implMap.put(Locale.ENGLISH , new YearDecoratorEnglish());
    implMap.put(Locale.JAPAN, new YearDecoratorJapanese());
  }
  
  public static String getOutputString(int year ,  Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(year); 
  }
}
