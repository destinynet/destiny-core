/**
 * @author smallufo 
 * Created on 2008/1/17 at 上午 1:35:01
 */ 
package destiny.core.calendar;

import com.google.common.collect.ImmutableMap;
import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class LocationDecorator
{
  private final static ImmutableMap<Locale , Decorator<Location>> implMap = new ImmutableMap.Builder<Locale , Decorator<Location>>()
    .put(Locale.TAIWAN , new LocationDecoratorTaiwan())
    .put(Locale.CHINA  , new LocationDecoratorChina())
    .put(Locale.ENGLISH, new LocationDecoratorEnglish())
    .build();
  
  @NotNull
  public static String getOutputString(Location location , Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(location);
  }
  
}
