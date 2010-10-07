/**
 * @author smallufo 
 * Created on 2008/4/2 at 上午 1:26:30
 */ 
package destiny.core;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

public class GenderDecorator
{
  private final static ImmutableMap<Locale , Decorator<Gender>> implMap = new ImmutableMap.Builder<Locale , Decorator<Gender>>()
    .put(Locale.TAIWAN , new GenderDecoratorChinese())
    .put(Locale.ENGLISH, new GenderDecoratorEnglish())
    .build();
  
  public static String getOutputString(Gender gender , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(gender);
  }
}
