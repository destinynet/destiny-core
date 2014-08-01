/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:15:48
 */ 
package destiny.core.calendar.decorators;

import com.google.common.collect.ImmutableMap;
import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MonthDecorator
{
  private final static ImmutableMap<Locale , Decorator<Integer>> implMap = new ImmutableMap.Builder<Locale , Decorator<Integer>>()
    .put(Locale.CHINESE , new MonthDecoratorChinese())
    .put(Locale.ENGLISH , new MonthDecoratorEnglish())
    .build();
  
  @NotNull
  public static String getOutputString(int value ,  Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(value);
  }
}
