/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:28:28
 */ 
package destiny.core.calendar.decorators;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class DayDecorator
{
  private final static ImmutableMap<Locale , Decorator<Integer>> implMap = new ImmutableMap.Builder<Locale , Decorator<Integer>>()
    .put(Locale.CHINESE , new DayDecoratorChinese())
    .put(Locale.ENGLISH , new DayDecoratorEnglish())
    .build();
  
  public static String getOutputString(int value ,  Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(value); 
  }
}
