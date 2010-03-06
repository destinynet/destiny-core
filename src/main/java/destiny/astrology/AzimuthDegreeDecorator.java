/**
 * @author smallufo 
 * Created on 2008/5/9 at 上午 5:27:04
 */ 
package destiny.astrology;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;

/** 將地平方位角的「度數」，轉化為亦讀的輸出，例如：北偏東10度 */
public class AzimuthDegreeDecorator
{
  private static Map<Locale , Decorator<Double>> implMap = Collections.synchronizedMap(new HashMap<Locale , Decorator<Double>>());
  static
  {
    implMap.put(Locale.CHINESE , new AzimuthDegreeTaiwanDecorator());
    implMap.put(Locale.ENGLISH , new AzimuthDegreeEnglishDecorator());
  }

  public static String getOutputString(Double value , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(value);
  }

}
