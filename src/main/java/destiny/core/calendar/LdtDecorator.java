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

public class LdtDecorator {

  private final static ImmutableMap<Locale , Decorator<LocalDateTime>> implMap = new ImmutableMap.Builder<Locale , Decorator<LocalDateTime>>()
    .put(Locale.TAIWAN , new LdtDecoratorChinese())
    .put(Locale.ENGLISH , new LdtDecoratorEnglish())
    .put(Locale.JAPAN, new LdtDecoratorJapanese())
    .build();

  @NotNull
  public static String getOutputString(LocalDateTime time , Locale locale)
  {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(time);
  }
}
