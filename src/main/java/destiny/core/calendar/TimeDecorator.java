/**
 * @author smallufo 
 * Created on 2008/1/16 at 下午 11:05:18
 */ 
package destiny.core.calendar;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class TimeDecorator
{
  private final static ImmutableMap<Locale , Decorator<Time>> implMap = new ImmutableMap.Builder<Locale , Decorator<Time>>()
    .put(Locale.TAIWAN , new TimeDecoratorChinese())
    .put(Locale.ENGLISH , new TimeDecoratorEnglish())
    .put(Locale.JAPAN, new TimeDecoratorJapanese())
    .build();
  
  public static String getOutputString(Time time , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(time);
  }
}
