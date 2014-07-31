/**
 * @author smallufo 
 * Created on 2008/1/15 at 下午 12:01:56
 */ 
package destiny.utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocaleUtilsTest extends TestCase
{
  @NotNull
  private Set<Locale> locales = new HashSet<>();
  private Locale locale;
  @Nullable
  private Locale matched;

  public void testGetBestMatchingLocale1()
  {
    locales.add(new Locale("zh" , "TW" , "AAA"));
    locales.add(new Locale("zh" , "TW" , "BBB"));
    locales.add(new Locale("zh" , "TW" , "CCC"));
    
    //完全符合
    locale = new Locale("zh" , "TW" , "AAA");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertEquals(new Locale("zh" , "TW" , "AAA") , matched);
    
    //完全符合 , 大小寫不同 視為符合
    locale = new Locale("ZH" , "tw" , "aaa");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertEquals(new Locale("zh" , "TW" , "AAA") , matched);
  }
  
  public void testGetBestMatchingLocale2()
  {
    locales.add(new Locale("zh" , "TW" , "AAA"));
    locales.add(new Locale("zh" , "TW" , "BBB"));
    locales.add(new Locale("zh" , "TW" , "CCC"));
    
    //只有 語言/國家 符合，不知道傳回來的是哪一個，總之不為空即可
    locale = new Locale("zh" , "TW");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertNotNull(matched);
    
    locale = new Locale("ZH" , "tw");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertNotNull(matched);
  }
  
  public void testGetBestMatchingLocale3()
  {
    locales.add(new Locale("zh" , "TW" , "AAA"));
    locales.add(new Locale("zh" , "TW" , "BBB"));
    locales.add(new Locale("zh" , "TW" , "CCC"));
    
    
    //只有 語言 符合，不知道傳回來的是哪一個，總之不為空即可
    locale = new Locale("zh");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertNotNull(matched);
    
    locale = new Locale("ZH");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertNotNull(matched);
  }
  
  public void testGetBestMatchingLocale4()
  {
    locales.add(new Locale("en" , "US" , "AAA"));
    locales.add(new Locale("en" , "US" , "BBB"));
    locales.add(new Locale("en" , "US" , "CCC"));
    
    locale = new Locale("zh" , "TW" , "AAA");
    matched = LocaleUtils.getBestMatchingLocale(locale, locales);
    assertNull(matched);
  }
}
