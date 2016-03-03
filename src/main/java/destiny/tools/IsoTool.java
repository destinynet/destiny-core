/**
 * Created by smallufo on 2016-03-01.
 */
package destiny.tools;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;

public class IsoTool implements Serializable {

  /** 小寫 2-letter 英文字母 , 例如 'zh' */
  private static final Set<String> ISO_LANGUAGES = ImmutableSet.copyOf(Locale.getISOLanguages());

  /** 大寫 2-letter 英文字母 , 例如 'TW' */
  private static final Set<String> ISO_COUNTRIES = ImmutableSet.copyOf(Locale.getISOCountries());

  /** 大小寫通吃，即使傳入 'ZH' 也會轉成 'zh' ，並且傳回 true */
  public static boolean isValidLanguage(@NotNull String s) {
    return ISO_LANGUAGES.contains(s.toLowerCase());
  }

  /** 大小寫通吃，即使傳入 'tw' 也會轉成 'TW' ，並且傳回 true */
  public static boolean isValidCountry(@NotNull String s) {
    return ISO_COUNTRIES.contains(s.toUpperCase());
  }
}
