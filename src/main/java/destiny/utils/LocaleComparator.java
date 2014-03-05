package destiny.utils;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by smallufo on 2014-03-05.
 * TODO : When migrating to Java8 , replace guava's Objects to java.util.Objects
 */
public class LocaleComparator implements Comparator<Locale>, Serializable {
  private final Locale locale;

  public LocaleComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(Locale locale1, Locale locale2) {
    int result;
    if (Objects.equal(locale1, locale2))
      result = 0;
    else if (Objects.equal(locale1, locale))
      result = -1;
    else if (Objects.equal(locale2, locale))
      result = 1;
    else if (Objects.equal(locale1.getLanguage(), locale.getLanguage()))
      result = -1;
    else if (Objects.equal(locale2.getLanguage(), locale.getLanguage()))
      result = 1;
    else if (Objects.equal(Locale.ENGLISH.getLanguage(), locale1.getLanguage()))
      result = -1;
    else if (Objects.equal(Locale.ENGLISH.getLanguage(), locale2.getLanguage()))
      result = 1;
    else
      result = 1;
    return result;
  }
}
