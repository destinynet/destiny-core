/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:28:28
 */
package destiny.core.calendar.decorators;

import com.google.common.collect.ImmutableMap;
import destiny.tools.Decorator;
import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class DayDecorator {

  private final static ImmutableMap<Locale, Decorator<Integer>> implMap = new ImmutableMap.Builder<Locale, Decorator<Integer>>()
    .put(Locale.CHINESE, new DayDecoratorChinese())
    .put(Locale.ENGLISH, new DayDecoratorEnglish())
    .build();

  @NotNull
  public static String getOutputString(int value, Locale locale) {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(value);
  }
}
