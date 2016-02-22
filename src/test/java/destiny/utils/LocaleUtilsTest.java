/**
 * @author smallufo 
 * Created on 2008/1/15 at 下午 12:01:56
 */ 
package destiny.utils;

import destiny.tools.LocaleUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class LocaleUtilsTest
{
  @NotNull
  private Set<Locale> locales = new HashSet<>();
  private Locale locale;

  private Optional<Locale> matched;

  @Test
  public void testGetBestMatchingLocale1()
  {
    locales.add(new Locale("zh" , "TW" , "AAA"));
    locales.add(new Locale("zh" , "TW" , "BBB"));
    locales.add(new Locale("zh" , "TW" , "CCC"));
    
    //完全符合
    locale = new Locale("zh" , "TW" , "AAA");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertEquals(new Locale("zh" , "TW" , "AAA") , matched.get());
    
    //完全符合 , 大小寫不同 視為符合
    locale = new Locale("ZH" , "tw" , "aaa");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertEquals(new Locale("zh" , "TW" , "AAA") , matched.get());
  }

  @Test
  public void testGetBestMatchingLocale2()
  {
    locales.add(new Locale("zh" , "TW" , "AAA"));
    locales.add(new Locale("zh" , "TW" , "BBB"));
    locales.add(new Locale("zh" , "TW" , "CCC"));
    
    //只有 語言/國家 符合，不知道傳回來的是哪一個，總之不為空即可
    locale = new Locale("zh" , "TW");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertTrue(matched.isPresent());

    locale = new Locale("ZH" , "tw");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertTrue(matched.isPresent());
  }

  @Test
  public void testGetBestMatchingLocale3()
  {
    locales.add(new Locale("zh" , "TW" , "AAA"));
    locales.add(new Locale("zh" , "TW" , "BBB"));
    locales.add(new Locale("zh" , "TW" , "CCC"));
    
    
    //只有 語言 符合，不知道傳回來的是哪一個，總之不為空即可
    locale = new Locale("zh");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertTrue(matched.isPresent());
    
    locale = new Locale("ZH");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertTrue(matched.isPresent());
  }

  @Test
  public void testGetBestMatchingLocale4()
  {
    locales.add(new Locale("en" , "US" , "AAA"));
    locales.add(new Locale("en" , "US" , "BBB"));
    locales.add(new Locale("en" , "US" , "CCC"));
    
    locale = new Locale("zh" , "TW" , "AAA");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertTrue(!matched.isPresent());
  }
}
