/**
 * @author smallufo
 * Created on 2008/1/15 at 上午 11:59:10
 */
package destiny.tools;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public class LocaleUtils implements Serializable {

  @NotNull
  public static String getString(@NotNull Map<Locale,String> localeStringMap , @NotNull Locale locale) {
    return LocaleTools.INSTANCE.getStringOrDefault(localeStringMap , locale);
  }

  @Deprecated
  public static Optional<Locale> getBestMatchingLocale(Locale locale) {
    //TODO : 未來該把此 TAIWAN , CHINA , ENGLISH 做更彈性的調整。
    ImmutableSet<Locale> supported = ImmutableSet.of(Locale.TAIWAN , Locale.CHINA , Locale.ENGLISH);
    return getBestMatchingLocale(locale , supported);
  }




  /**
   * 從 locales 中，找尋最符合 locale 的 , 如果找不到，會以系統內定 locale 與 locales 比對 <br/>
   * 如果 locale 為 null , 程式會以系統內定的 locale 取代
   * <pre>
   * 1. 目標語言(language)＋目標國家(country)＋目標變數(variant)
   * 2. 目標語言(language)＋目標國家(country)
   * 3. 目標語言(language)
   * 4. 系統語言(language)＋系統國家(country)＋系統變數(variant)
   * 5. 系統語言(language)＋系統國家(country)
   * 6. 系統語言(language)
   * 7. 內訂(純 basename)
   * </pre>
   */
  public static Optional<Locale> getBestMatchingLocale(@Nullable Locale locale , @NotNull Iterable<Locale> locales) {
    return Optional.ofNullable(LocaleTools.INSTANCE.getBestMatchingLocaleWithDefault(locales, locale ==  null ? Locale.getDefault(): locale));
  }
}
