/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:49:59
 */ 
package destiny.core.calendar.decorators;

import com.google.common.collect.ImmutableMap;
import destiny.tools.Decorator;
import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class HourDecorator
{
  private final static ImmutableMap<Locale , Decorator<Integer>> implMap = new ImmutableMap.Builder<Locale , Decorator<Integer>>()
    .put(Locale.TAIWAN  , new HourDecoratorTaiwan())
    .put(Locale.CHINESE , new HourDecoratorChina())
    .put(Locale.ENGLISH , new HourDecoratorEnglish())
    .build();
  
  @NotNull
  public static String getOutputString(int value ,  Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(value);
  }
}
