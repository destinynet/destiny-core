/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.tools

import java.util.*

object LocaleTools {

  /**
   * 解析語言字串。委派給 [Lang.of]，因此正確辨識 BCP-47 的 script 子標籤。
   *
   * ## 2026-08-03 修正
   *
   * 舊版按**位置**切分成 language / country / variant，因此把
   * `"zh-Hant-TW"`（BCP-47 的合法寫法，CLDR / Android / iOS 對繁中的正規形式）
   * 誤解成 `country=HANT, variant=TW`，其 language tag 會變成 `zh-x-lvariant-TW`，
   * 後續一切 [ResourceBundle] 查找靜默落空、退回預設語言。
   * 而 `AbstractLineService` 會把這個結果經 `destinyUserDao.getOrCreate()` 寫進 DB。
   *
   * 修正後的附帶差異（皆為改善，`LocaleToolsTest.getLocale` 有逐項釘住）：
   *
   * | 輸入 | 舊 | 新 |
   * |---|---|---|
   * | `zh-Hant-TW` | `zh_HANT_TW`（壞） | `zh_Hant_TW` |
   * | `zh_TW_TAIPEI` | variant `TAIPEI` | variant `taipei`（BCP-47 正規大小寫） |
   * | `1` / `zh1` 等非法語言碼 | 產生 tag 為 `und` 的畸形 Locale | `null` |
   * | `und` | language 為 `"und"` 的 Locale | [Locale.ROOT] |
   *
   * 常見輸入（`zh`、`zh_TW`、`zh-TW`、`en_US`、`ZH#TAIWAN`、`zh_HK_#Hant` …）
   * 行為完全不變。所有呼叫端本來就處理 null（回傳型別一向可空）。
   */
  fun getLocale(input: String?): Locale? = Lang.of(input)?.toLocale()


  fun parseAcceptLanguageHeader(acceptLanguage: String?, default: Locale = Locale.TRADITIONAL_CHINESE): Locale {
    if (acceptLanguage.isNullOrBlank()) return default
    return try {
      val primaryLang = acceptLanguage.split(',')
        .firstOrNull()
        ?.split(';')
        ?.firstOrNull()
        ?.trim()
      primaryLang?.let { Locale.forLanguageTag(it) } ?: default
    } catch (_: Exception) {
      default
    }
  }

  /**
   * Converts a Locale to a priority-ordered list of locale strings for DB queries.
   *
   * Order: primary language → country variant → cross-language fallbacks.
   *
   * e.g. Locale("en")       → ["en", "zh"]
   * e.g. Locale("zh", "TW") → ["zh", "zh_TW", "en"]
   * e.g. Locale("ja", "JP") → ["ja", "ja_JP", "zh", "en"]
   * e.g. Locale("ko")       → ["ko", "en", "zh"]
   * e.g. Locale("fr")       → ["fr"]
   */
  fun buildLocaleList(locale: Locale): List<String> {
    val lang = locale.language
    val country = locale.country

    val primary = mutableListOf(lang)
    if (country.isNotEmpty()) {
      val variant = "${lang}_${country}"
      if (variant != lang) primary.add(variant)
    }

    val fallback = when (lang) {
      "en" -> listOf("zh")
      "zh" -> listOf("en")
      "ja" -> listOf("zh", "en")
      "ko" -> listOf("en", "zh")
      else -> emptyList()
    }

    return primary + fallback
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
      , Locale.of(locale.language, locale.country)  // 第二項
      , Locale.of(locale.language) // 第三項
    )

    expandedLocales
      .filter { localeStringMap.containsKey(it) }
      .forEach {
        return localeStringMap.getValue(it)
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
    return getString(localeStringMap, locale) ?: run {
      val bestMatchingLocale = getBestMatchingLocale(locale, localeStringMap.keys)
      bestMatchingLocale?.let {
        localeStringMap[bestMatchingLocale]
      } ?: localeStringMap.values.first()
    }

  }


  /**
   * 從 locales 中，找尋最符合 locale 的
   * <pre>
   * 1. 目標語言(language)＋目標國家(country)＋目標變數(variant)
   * 2. 目標語言(language)＋目標國家(country)
   * 3. 目標語言(language)
   * </pre>
   */
  fun getBestMatchingLocale(locale: Locale = defaultLocale, locales: Iterable<Locale>): Locale? {

    return locales.let { locs ->
      locs.firstOrNull {
        //符合第一項 : 語言/國家/變數 都符合
        locale.language.equals(it.language, ignoreCase = true) &&
          locale.country.equals(it.country, ignoreCase = true) &&
          locale.variant.equals(it.variant, ignoreCase = true)
      }?: run {
        locs.firstOrNull {
          //符合第二項 : 語言/國家 符合即可
          locale.language.equals(it.language, ignoreCase = true) &&
            locale.country.equals(it.country, ignoreCase = true)
        }
      }?: run {
        locs.firstOrNull {
          //符合第三項 : 只有語言符合
          locale.language.equals(it.language, ignoreCase = true)
        }
      }
    }
  }

  /**
   * @param locales : 找尋最符合的 locale , 如果找不到，則採取此 locales 第一個
   */
  fun getBestMatchingLocaleOrFirst(locale: Locale = defaultLocale, locales: Iterable<Locale>): Locale {
    return getBestMatchingLocale(locale, locales) ?: locales.first()
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
  fun getBestMatchingLocaleWithDefault(locales: Iterable<Locale>,
                                       locale: Locale = defaultLocale): Locale? {
    return getBestMatchingLocale(locale, locales) ?: getBestMatchingLocale(defaultLocale, locales)
  }


}
