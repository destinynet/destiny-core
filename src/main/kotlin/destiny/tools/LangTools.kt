/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

/**
 * [LocaleTools] 的平台中立版本 —— 以 [Lang] 取代 `java.util.Locale`。
 *
 * 兩者**並存**：`LocaleTools` 原封不動（11 個模組 / 39 個檔案在用），
 * 本物件供新程式碼與階段 2 的遷移使用。`LangToolsTest` 對使用中的語言
 * 逐一驗證兩者行為一致，並明確標出刻意不同之處。
 *
 * ## 與 [LocaleTools] 刻意不同的地方
 *
 *  1. **三段輸入**：`LocaleTools.getLocale("zh-Hant-TW")` 按位置硬塞成
 *     language/country/variant，得到 `country=HANT, variant=TW` 這種壞掉的
 *     Locale（其 language tag 會變成 `zh-x-lvariant-TW`）。
 *     [Lang.of] 則正確辨識 `Hant` 為 script。
 *  2. **底線分隔**：`Locale.forLanguageTag("zh_TW")` 回傳空 Locale（Java 只吃
 *     連字號），[Lang.of] 兩種都吃。
 *  3. **`Accept-Language: *`**：Locale 版得到 `ROOT`（等於「未定語言」），
 *     Lang 版視為無法解析而回傳 default —— 後者才是呼叫端要的。
 *
 * 以上三點都是修正而非退步。
 */
object LangTools {

  /**
   * 解析 HTTP `Accept-Language` 標頭，取第一順位語言。
   *
   * 例：`"zh-TW,zh;q=0.9,en;q=0.8"` → [Lang.ZH_TW]
   */
  fun parseAcceptLanguageHeader(acceptLanguage: String?, default: Lang = Lang.DEFAULT): Lang {
    if (acceptLanguage.isNullOrBlank()) return default
    val primary = acceptLanguage.split(',').firstOrNull()
      ?.split(';')?.firstOrNull()
      ?.trim()
    return Lang.of(primary) ?: default
  }

  /**
   * 轉為 DB 查詢用的、依優先序排列的語言字串清單。
   *
   * 輸出格式與 [LocaleTools.buildLocaleList] 完全相同（底線形式）：
   * ```
   * en    → ["en", "zh"]
   * zh-TW → ["zh", "zh_TW", "en"]
   * ja-JP → ["ja", "ja_JP", "zh", "en"]
   * ko    → ["ko", "en", "zh"]
   * fr    → ["fr"]
   * ```
   */
  fun buildLangList(lang: Lang): List<String> {
    val language = lang.language
    val region = lang.region

    val primary = mutableListOf(language)
    if (region != null) {
      val withRegion = "${language}_${region}"
      if (withRegion != language) primary.add(withRegion)
    }

    val fallback = when (language) {
      "en"  -> listOf("zh")
      "zh"  -> listOf("en")
      "ja"  -> listOf("zh", "en")
      "ko"  -> listOf("en", "zh")
      else  -> emptyList()
    }

    return primary + fallback
  }

  /**
   * 在 [map] 中依「語言＋地區＋variant → 語言＋地區 → 語言」的順序找值。
   *
   * 注意這與 [Lang.fallbacks] 不同：後者是 resource bundle 的查找鏈（會補上
   * [Lang.ROOT]），此處則是 [LocaleTools.getString] 的三層展開語意。
   */
  fun getString(map: Map<Lang, String>, lang: Lang): String? {
    return expand(lang).firstNotNullOfOrNull { map[it] }
  }

  /**
   * 同 [getString]，但保證有值：找不到就退回最佳匹配，再找不到就取 [map] 的第一個值。
   *
   * @throws NoSuchElementException [map] 為空時（沿用 [LocaleTools.getStringOrDefault] 的行為）
   */
  fun getStringOrDefault(map: Map<Lang, String>, lang: Lang): String {
    return getString(map, lang)
      ?: getBestMatchingLang(lang, map.keys)?.let { map[it] }
      ?: map.values.first()
  }

  /**
   * 從 [langs] 中找出最符合 [lang] 者：
   * 語言＋地區＋variant → 語言＋地區 → 僅語言。找不到則 null。
   */
  fun getBestMatchingLang(lang: Lang, langs: Iterable<Lang>): Lang? {
    return langs.firstOrNull { it.language == lang.language && it.region == lang.region && it.variants == lang.variants }
      ?: langs.firstOrNull { it.language == lang.language && it.region == lang.region }
      ?: langs.firstOrNull { it.language == lang.language }
  }

  /** 同 [getBestMatchingLang]，找不到時取 [langs] 的第一個 */
  fun getBestMatchingLangOrFirst(lang: Lang, langs: Iterable<Lang>): Lang =
    getBestMatchingLang(lang, langs) ?: langs.first()

  /** 同 [getBestMatchingLang]，找不到時再以 [Lang.DEFAULT] 比對一次 */
  fun getBestMatchingLangWithDefault(langs: Iterable<Lang>, lang: Lang = Lang.DEFAULT): Lang? =
    getBestMatchingLang(lang, langs) ?: getBestMatchingLang(Lang.DEFAULT, langs)

  /** 三層展開，去重後保序 */
  private fun expand(lang: Lang): List<Lang> {
    val language = lang.language
    val region = lang.region
    return listOfNotNull(
      lang,
      if (region != null) Lang.of("$language-$region") else null,
      Lang.of(language),
    ).distinct()
  }
}
