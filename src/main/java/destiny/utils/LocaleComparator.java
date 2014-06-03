package destiny.utils;



import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by smallufo on 2014-03-05.
 */
public class LocaleComparator implements Comparator<Locale>, Serializable {
  private final Locale locale;

  public LocaleComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(Locale locale1, Locale locale2) {
    int result;
    if (Objects.equals(locale1, locale2))
      result = 0;
    else if (Objects.equals(locale1, locale))
      result = -1;
    else if (Objects.equals(locale2, locale))
      result = 1;
    else if (Objects.equals(locale1.getLanguage(), locale.getLanguage()))
      result = -1;
    else if (Objects.equals(locale2.getLanguage(), locale.getLanguage()))
      result = 1;
    else if (Objects.equals(Locale.ENGLISH.getLanguage(), locale1.getLanguage()))
      result = -1;
    else if (Objects.equals(Locale.ENGLISH.getLanguage(), locale2.getLanguage()))
      result = 1;
    else
      result = 1;
    return result;
  }
}
