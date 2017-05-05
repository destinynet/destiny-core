/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar;

import com.google.common.collect.ImmutableMap;
import destiny.tools.Decorator;
import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * 純粹只有經緯度，以及時區
 */
public class LngLatDecorator {

  private final static ImmutableMap<Locale , Decorator<Location>> implMap = new ImmutableMap.Builder<Locale , Decorator<Location>>()
    .put(Locale.TAIWAN , new LngLatDecoratorTaiwan())
    .put(Locale.CHINA  , new LngLatDecoratorChina())
    .put(Locale.ENGLISH, new LngLatDecoratorEnglish())
    .build();

  @NotNull
  public static String getOutputString(Location location , Locale locale) {
    return implMap.get(
      LocaleUtils.getBestMatchingLocale(locale, implMap.keySet()).orElse((Locale) implMap.keySet().toArray()[0])
    ).getOutputString(location);
  }
}
