/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar;

import com.google.common.collect.ImmutableMap;
import destiny.tools.Decorator;
import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Locale;

public class TimeDecorator {

  private final static ImmutableMap<Locale , Decorator<LocalDateTime>> implMap = new ImmutableMap.Builder<Locale , Decorator<LocalDateTime>>()
    .put(Locale.TAIWAN , new TimeDecoratorChinese())
    .put(Locale.ENGLISH , new TimeDecoratorEnglish())
    .put(Locale.JAPAN, new TimeDecoratorJapanese())
    .build();

  @NotNull
  public static String getOutputString(LocalDateTime time , Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(time);
  }
}
