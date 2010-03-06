/**
 * @author smallufo 
 * Created on 2008/1/15 at 上午 12:55:13
 */ 
package destiny.astrology;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.LocaleUtils;

public class ZodiacDegreeDecorator
{
  private static Map<Locale , ZodiacDegreeDecoratorIF> implMap = Collections.synchronizedMap(new HashMap<Locale , ZodiacDegreeDecoratorIF>());
  static
  {
    implMap.put(Locale.TAIWAN , new ZodiacDegreeDecoratorTradChinese());
    implMap.put(Locale.US     , new ZodiacDegreeDecoratorEnglish());
  }
  
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
