/**
 * @author smallufo 
 * Created on 2008/1/15 at 上午 12:55:13
 */ 
package destiny.astrology;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.LocaleUtils;

public class ZodiacDegreeDecorator
{
  private final static ImmutableMap<Locale , ZodiacDegreeDecoratorIF> implMap = new ImmutableMap.Builder<Locale , ZodiacDegreeDecoratorIF>()
    .put(Locale.TAIWAN , new ZodiacDegreeDecoratorTradChinese())
    .put(Locale.US     , new ZodiacDegreeDecoratorEnglish())
    .build();

  public static String getOutputString(double degree , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(degree);
  }
  
  public static String getSimpOutputString(double degree , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getSimpOutString(degree);
  }
  
}
