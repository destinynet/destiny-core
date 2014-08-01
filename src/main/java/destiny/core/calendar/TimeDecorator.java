/**
 * @author smallufo 
 * Created on 2008/1/16 at 下午 11:05:18
 */ 
package destiny.core.calendar;

import com.google.common.collect.ImmutableMap;
import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class TimeDecorator
{
  private final static ImmutableMap<Locale , Decorator<Time>> implMap = new ImmutableMap.Builder<Locale , Decorator<Time>>()
    .put(Locale.TAIWAN , new TimeDecoratorChinese())
    .put(Locale.ENGLISH , new TimeDecoratorEnglish())
    .put(Locale.JAPAN, new TimeDecoratorJapanese())
    .build();
  
  @NotNull
  public static String getOutputString(Time time , Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(time);
  }
}
