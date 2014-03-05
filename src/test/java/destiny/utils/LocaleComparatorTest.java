package destiny.utils;

import org.junit.Test;

import java.util.*;

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

  @Test
  public void testLangMap() throws Exception {
    Map<Locale , String> map = new HashMap<Locale , String>(){{
      put(Locale.TRADITIONAL_CHINESE, "導演");
      put(Locale.SIMPLIFIED_CHINESE,  "导演");
      put(Locale.ENGLISH, "Director");
    }};

    Locale locale;
    locale = Locale.TRADITIONAL_CHINESE;
    assert ("導演".equals(map.get(map.keySet().stream().sorted(new LocaleComparator(locale)).findFirst().get())));

    locale = Locale.SIMPLIFIED_CHINESE;
    assert ("导演".equals(map.get(map.keySet().stream().sorted(new LocaleComparator(locale)).findFirst().get())));

    locale = Locale.ENGLISH;
    assert ("Director".equals(map.get(map.keySet().stream().sorted(new LocaleComparator(locale)).findFirst().get())));

    locale = Locale.FRANCE;
    assert ("Director".equals(map.get(map.keySet().stream().sorted(new LocaleComparator(locale)).findFirst().get())));
  }
}
