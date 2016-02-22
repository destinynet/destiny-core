/**
 * @author smallufo 
 * Created on 2008/1/15 at 上午 12:55:13
 */ 
package destiny.astrology;

import com.google.common.collect.ImmutableMap;
import destiny.tools.LocaleUtils;

import java.util.Locale;

public class ZodiacDegreeDecorator
{
  private final static ImmutableMap<Locale , ZodiacDegreeDecoratorIF> implMap = new ImmutableMap.Builder<Locale , ZodiacDegreeDecoratorIF>()
    .put(Locale.TAIWAN , new ZodiacDegreeDecoratorTradChinese())
    .put(Locale.US     , new ZodiacDegreeDecoratorEnglish())
    .build();

  public static String getOutputString(double degree , Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(degree);
  }
  
  public static String getSimpOutputString(double degree , Locale locale)
  {return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getSimpOutString(degree);
  }
  
}
