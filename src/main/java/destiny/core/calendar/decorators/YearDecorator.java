/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 12:31:36
 */ 
package destiny.core.calendar.decorators;

import com.google.common.collect.ImmutableMap;
import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class YearDecorator
{
  private final static ImmutableMap<Locale , Decorator<Integer>> implMap = new ImmutableMap.Builder<Locale , Decorator<Integer>>()
    .put(Locale.TAIWAN  , new YearDecoratorTaiwan())
    .put(Locale.CHINESE , new YearDecoratorChinese())
    .put(Locale.ENGLISH , new YearDecoratorEnglish())
    .put(Locale.JAPAN, new YearDecoratorJapanese())
    .build();
  
  @NotNull
  public static String getOutputString(int year ,  Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(year);
  }
}
