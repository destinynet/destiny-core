/**
 * @author smallufo 
 * Created on 2008/1/17 at 上午 1:35:01
 */ 
package destiny.core.calendar;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class LocationDecorator
{
  private final static ImmutableMap<Locale , Decorator<Location>> implMap = new ImmutableMap.Builder<Locale , Decorator<Location>>()
    .put(Locale.TAIWAN , new LocationDecoratorTaiwan())
    .put(Locale.CHINA  , new LocationDecoratorChina())
    .put(Locale.ENGLISH, new LocationDecoratorEnglish())
    .build();
  
  public static String getOutputString(Location location , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(location);
  }
  
}
