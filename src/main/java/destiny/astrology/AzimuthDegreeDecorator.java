/**
 * @author smallufo 
 * Created on 2008/5/9 at 上午 5:27:04
 */ 
package destiny.astrology;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.utils.Decorator;
import destiny.utils.LocaleUtils;
import org.jetbrains.annotations.Nullable;

/** 將地平方位角的「度數」，轉化為亦讀的輸出，例如：北偏東10度 */
public class AzimuthDegreeDecorator
{
  private final static ImmutableMap<Locale , Decorator<Double>> implMap = new ImmutableMap.Builder<Locale , Decorator<Double>>()
    .put(Locale.CHINESE , new AzimuthDegreeTaiwanDecorator())
    .put(Locale.ENGLISH , new AzimuthDegreeEnglishDecorator())
    .build();
  
  @Nullable
  public static String getOutputString(Double value , Locale locale)
  {
    Locale matched = LocaleUtils.getBestMatchingLocale(locale , implMap.keySet());
    if (matched == null)
      matched = (Locale) implMap.keySet().toArray()[0];
    return implMap.get(matched).getOutputString(value);
  }

}
