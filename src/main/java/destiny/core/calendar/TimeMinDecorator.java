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

/** 只輸出到「分」 */
public class TimeMinDecorator {

  private final static ImmutableMap<Locale , Decorator<LocalDateTime>> implMap = new ImmutableMap.Builder<Locale , Decorator<LocalDateTime>>()
    .put(Locale.TAIWAN , new TimeMinDecoratorChinese())
    .put(Locale.ENGLISH , new TimeMinDecoratorEnglish())
    .put(Locale.JAPAN, new TimeMinDecoratorJapanese())
    .build();

  @NotNull
  public static String getOutputString(LocalDateTime time , Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(time);
  }
}
