/**
 * @author smallufo 
 * Created on 2008/1/15 at 上午 11:59:10
 */ 
package destiny.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableSet;

public class LocaleUtils implements Serializable
{

  /**
   * <pre>
   * 在 localeStringMap 中，給予特定的 locale，找出其值(String)
   * Locale 的搜尋順序，與 ResourceBundle 一樣 :
   * 
   * 1. 目標語言(language)＋目標國家(country)＋目標變數(variant)
   * 2. 目標語言(language)＋目標國家(country)
   * 3. 目標語言(language)
   * 4. 系統語言(language)＋系統國家(country)＋系統變數(variant)
   * 5. 系統語言(language)＋系統國家(country)
   * 6. 系統語言(language)
   * 7. 內訂(純 basename)
   * </pre>
   */
  public static String getString(Map<Locale,String> localeStringMap , Locale locale)
  {
    if (locale == null)
      throw new NullPointerException("locale cannot be null");
    
    Locale defaultLocale = Locale.getDefault();
    
    List<Locale> sixLocales = Collections.synchronizedList(new LinkedList<Locale>());
    sixLocales.add(locale); //第一項
    sixLocales.add(new Locale(locale.getLanguage() , locale.getCountry())); //第二項
    sixLocales.add(new Locale(locale.getLanguage())); //第三項
    sixLocales.add(defaultLocale); //第四項
    sixLocales.add(new Locale(defaultLocale.getLanguage() , defaultLocale.getCountry())); //第五項
    sixLocales.add(new Locale(defaultLocale.getLanguage())); //第六項
    
    Iterator<Locale> it = sixLocales.iterator();
    while(it.hasNext())
    {
      Locale eachLocale = it.next();
      if (localeStringMap.containsKey(eachLocale))
        return localeStringMap.get(eachLocale);
    }
      
    //前面都找不到，最後，把 basename 傳回去 
    return localeStringMap.get(null); //第七項
  }
  
  public static Locale getBestMatchingLocale(Locale locale)
  {
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
  public static Locale getBestMatchingLocale(Locale locale , Iterable<Locale> locales)
  {
    Locale defaultLocale = Locale.getDefault();
    if (locale == null)
      locale = defaultLocale;
    
    //符合第一項 : 語言/國家/變數 都符合
    //System.out.println("完全比對 :");
    for(Locale each : locales)
    {
      //System.out.println("\t比對" + locale + " 與 " + each);
      if(locale.getLanguage().equalsIgnoreCase(each.getLanguage()) &&
          locale.getCountry().equalsIgnoreCase(each.getCountry()) &&
          locale.getVariant().equalsIgnoreCase(each.getVariant()))
        return each;
    }
    
    //符合第二項 : 語言/國家 符合即可
    //System.out.println("語言/國家 比對 :");
    for(Locale each : locales)
    {
      //System.out.println("\t比對" + locale + " 與 " + each);
      if(locale.getLanguage().equalsIgnoreCase(each.getLanguage()) && 
          locale.getCountry().equalsIgnoreCase(each.getCountry()))
        return each;
    }
    
    //符合第三項 : 只有語言符合
    //System.out.println("語言 比對 :");
    for(Locale each : locales)
    {
      //System.out.println("\t比對" + locale + " 與 " + each);
      if(locale.getLanguage().equalsIgnoreCase(each.getLanguage()))
        return each;
    }
    
    //符合第四項 : 與系統內定Locale 的 語言/國家/變數 都符合
    //System.out.println("第四項 比對 :");
    for(Locale each : locales)
    {
      //System.out.println("\t比對系統的 " + defaultLocale + " 與 " + each);
      if(defaultLocale.getLanguage().equalsIgnoreCase(each.getLanguage()) &&
          defaultLocale.getCountry().equalsIgnoreCase(each.getCountry()) &&
          defaultLocale.getVariant().equalsIgnoreCase(each.getVariant()))
        return each;
    }
    
    //符合第五項 : 與系統內定Locale 的 語言/國家 符合即可
    //System.out.println("第五項 比對 :");
    for(Locale each : locales)
    {
      //System.out.println("\t比對系統的 " + defaultLocale + " 與 " + each);
      if(defaultLocale.getLanguage().equalsIgnoreCase(each.getLanguage()) && 
          defaultLocale.getCountry().equalsIgnoreCase(each.getCountry()))
        return each;
    }
    
    //符合第六項 : 與系統內定Locale 的語言符合即可
    //System.out.println("第六項 比對 :");
    for(Locale each : locales)
    {
      //System.out.println("\t比對系統的 " + defaultLocale + " 與 " + each);
      if(defaultLocale.getLanguage().equalsIgnoreCase(each.getLanguage()))
        return each;
    }
    
    //都找不到
    return null;
  }
}
