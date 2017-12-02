package destiny.tools

import java.util.*

/**
 * Created by smallufo on 2017-12-02.
 */
object LocaleTools {
  /** 將 string 以 _ 切開，傳回 Locale 物件  */
  fun getLocale(string: String): Locale {
    val st = StringTokenizer(string, "_")
    val lang: String
    val country: String
    val variant: String

    lang = st.nextToken()

    if (st.hasMoreTokens())
      country = st.nextToken()
    else
      return Locale(lang)

    if (st.hasMoreTokens())
      variant = st.nextToken()
    else
      return Locale(lang, country)

    return Locale(lang, country, variant)
  }

  /**
   * <pre>
   * 在 localeStringMap 中，給予特定的 locale，找出其值(String)
   * Locale 的搜尋順序，與 ResourceBundle 一樣 :
   *
   * @param locale : 目標語言 , 把 locale 展開三層
   * @param localeStringMap : 把展開的 三個 locale 去比對此 map ，看是否有符合的 key , 抓出其值
   *
   * 1. 目標語言(language)＋目標國家(country)＋目標變數(variant)
   * 2. 目標語言(language)＋目標國家(country)
   * 3. 目標語言(language)
   * </pre>
   */
  fun getString(localeStringMap: Map<Locale, String>, locale: Locale): String? {

    // 將欲搜尋的 locale 展開
    val expandedLocales = listOf(
      locale // 第一項
      , Locale(locale.language, locale.country)  // 第二項
      , Locale(locale.language) // 第三項
    )

    expandedLocales
      .filter { localeStringMap.containsKey(it) }
      .forEach {
        return localeStringMap[it]!!
      }

    return null
  }

  /**
   * 一定要從 langMap 當中找到 對應 locale 的詞句 ,
   * 若找不到 , 從 defaultLocale 找 , 再找不到 , 撈第一個
   *
   *
   */
  fun getStringOrDefault(localeStringMap: Map<Locale, String>, locale: Locale): String {
    return getString(localeStringMap , locale)?: {
      val bestMatchingLocale = getBestMatchingLocale(locale , localeStringMap.keys)
      bestMatchingLocale?.let {
        return@let localeStringMap[bestMatchingLocale]
      }?:localeStringMap.values.first()
    }.invoke()

  }


  /**
   * 從 locales 中，找尋最符合 locale 的
   * <pre>
   * 1. 目標語言(language)＋目標國家(country)＋目標變數(variant)
   * 2. 目標語言(language)＋目標國家(country)
   * 3. 目標語言(language)
   * </pre>
   */
  fun getBestMatchingLocale(locale: Locale = Locale.getDefault(), locales: Iterable<Locale>): Locale? {
    //符合第一項 : 語言/國家/變數 都符合
    locales
      .filter {
        locale.language.equals(it.language, ignoreCase = true) &&
          locale.country.equals(it.country, ignoreCase = true) &&
          locale.variant.equals(it.variant, ignoreCase = true)
      }
      .forEach { return it }

    //符合第二項 : 語言/國家 符合即可
    locales
      .filter {
        locale.language.equals(it.language, ignoreCase = true) &&
          locale.country.equals(it.country, ignoreCase = true)
      }
      .forEach { return it }

    //符合第三項 : 只有語言符合
    locales
      .filter {
        locale.language.equals(it.language, ignoreCase = true)
      }
      .forEach { return it }

    return null
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
  fun getBestMatchingLocaleWithDefault(locale: Locale = Locale.getDefault(), locales: Iterable<Locale>): Locale? {

    val defaultLocale = Locale.getDefault()

    return getBestMatchingLocale(locale , locales)?: getBestMatchingLocale(defaultLocale , locales)

  }


}