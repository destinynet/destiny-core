/**
 * @author smallufo 
 * Created on 2008/5/9 at 上午 5:27:04
 */ 
package destiny.astrology;

import com.google.common.collect.ImmutableMap;
import destiny.tools.Decorator;
import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

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
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(value);
  }

}
