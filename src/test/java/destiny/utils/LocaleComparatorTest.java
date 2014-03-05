package destiny.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by smallufo on 2014-03-05.
 */
public class LocaleComparatorTest {

  @Test
  public void testCompare() throws Exception {
    Locale[] locales;
    locales = new Locale[]{Locale.ENGLISH, Locale.TAIWAN, Locale.SIMPLIFIED_CHINESE, Locale.FRANCE};

    List<Locale> list;

    System.out.println("\nDefault : " + Locale.TRADITIONAL_CHINESE);
    list = Arrays.asList(locales);
    list.sort(new LocaleComparator(Locale.TRADITIONAL_CHINESE));
    System.out.println(list);


    System.out.println("\nDefault : " + Locale.SIMPLIFIED_CHINESE);
    list = Arrays.asList(locales);
    list.sort(new LocaleComparator(Locale.SIMPLIFIED_CHINESE));
    System.out.println(list);

    System.out.println("\nDefault : " + Locale.ENGLISH);
    list = Arrays.asList(locales);
    list.sort(new LocaleComparator(Locale.ENGLISH));
    System.out.println(list);

    System.out.println("\nDefault : " + Locale.FRENCH);
    list = Arrays.asList(locales);
    list.sort(new LocaleComparator(Locale.FRENCH));
    System.out.println(list);

  }
}
