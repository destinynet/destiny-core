/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 12:31:36
 */ 
package destiny.core.calendar.decorators;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class YearDecorator
{
  private final static ImmutableMap<Locale , Decorator<Integer>> implMap = new ImmutableMap.Builder<Locale , Decorator<Integer>>()
    .put(Locale.TAIWAN  , new YearDecoratorTaiwan())
    .put(Locale.CHINESE , new YearDecoratorChinese())
    .put(Locale.ENGLISH , new YearDecoratorEnglish())
    .put(Locale.JAPAN, new YearDecoratorJapanese())
    .build();
  
  public static String getOutputString(int year ,  Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(year); 
  }
}
