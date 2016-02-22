/**
 * @author smallufo
 * Created on 2008/4/2 at 上午 1:26:30
 */
package destiny.core;

import com.google.common.collect.ImmutableMap;
import destiny.tools.Decorator;
import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GenderDecorator {
  private final static ImmutableMap<Locale , Decorator<Gender>> implMap = new ImmutableMap.Builder<Locale , Decorator<Gender>>()
    .put(Locale.TAIWAN , new GenderDecoratorChinese())
    .put(Locale.ENGLISH, new GenderDecoratorEnglish())
    .build();

  @NotNull
  public static String getOutputString(Gender gender , Locale locale) {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(gender);
  }
}
