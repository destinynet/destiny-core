/**
 * @author smallufo 
 * Created on 2008/4/2 at 上午 1:26:30
 */ 
package destiny.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class GenderDecorator
{
  private static Map<Locale , Decorator<Gender>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Gender>>());
  static
  {
    implMap.put(Locale.TAIWAN , new GenderDecoratorChinese());
    implMap.put(Locale.ENGLISH, new GenderDecoratorEnglish());
  }
  
  public static String getOutputString(Gender gender , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(gender);
  }
}
