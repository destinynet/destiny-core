/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

/**
 * [LangTools] 必須與 [LocaleTools] 行為一致 —— 除了刻意修正的三處（見下方最後一節）。
 *
 * 這組測試的價值在於「比照既有實作」而非「自說自話」：每個函式都同時跑
 * Locale 版與 Lang 版，斷言兩者結果相同。
 */
class LangToolsTest {

  /** 不含 script、不含 3 段 variant 的語言 —— 兩實作應完全一致 */
  private val comparable = listOf(
    Locale.TAIWAN, Locale.CHINA, Locale.ENGLISH, Locale.US, Locale.UK,
    Locale.JAPAN, Locale.of("ja"), Locale.of("zh"), Locale.KOREA, Locale.of("ko"),
    Locale.FRANCE, Locale.of("fr"), Locale.GERMANY, Locale.ITALY, Locale.ROOT,
  )

  // ------------------------------------------------------- parseAcceptLanguageHeader

  @Test
  fun `parseAcceptLanguageHeader 與 LocaleTools 一致`() {
    val headers = listOf(
      "zh-TW,zh;q=0.9,en;q=0.8",
      "en-US,en;q=0.9",
      "ja",
      " en ",
      "ko-KR",
      "fr-FR,fr;q=0.7",
      null,
      "",
      "   ",
    )
    for (h in headers) {
      val viaLocale = LocaleTools.parseAcceptLanguageHeader(h, Locale.TRADITIONAL_CHINESE)
      val viaLang = LangTools.parseAcceptLanguageHeader(h, Lang.ZH_TW)
      assertEquals(viaLocale.toLang(), viaLang, "header = ${h?.let { "\"$it\"" }}")
    }
  }

  @Test
  fun `parseAcceptLanguageHeader 取第一順位並忽略 q 值`() {
    assertEquals(Lang.ZH_TW, LangTools.parseAcceptLanguageHeader("zh-TW,zh;q=0.9,en;q=0.8"))
    assertEquals(Lang.of("en-US"), LangTools.parseAcceptLanguageHeader("en-US;q=1.0,en;q=0.9"))
    assertEquals(Lang.JA, LangTools.parseAcceptLanguageHeader("  ja  "))
  }

  @Test
  fun `parseAcceptLanguageHeader 空值回傳 default`() {
    assertEquals(Lang.DEFAULT, LangTools.parseAcceptLanguageHeader(null))
    assertEquals(Lang.DEFAULT, LangTools.parseAcceptLanguageHeader(""))
    assertEquals(Lang.EN, LangTools.parseAcceptLanguageHeader("  ", default = Lang.EN))
  }

  // ------------------------------------------------------- buildLangList

  @Test
  fun `buildLangList 與 buildLocaleList 完全一致`() {
    for (locale in comparable) {
      assertContentEquals(
        LocaleTools.buildLocaleList(locale),
        LangTools.buildLangList(locale.toLang()),
        "buildLangList of $locale"
      )
    }
  }

  @Test
  fun `buildLangList 的實際輸出`() {
    assertContentEquals(listOf("en", "zh"), LangTools.buildLangList(Lang.EN))
    assertContentEquals(listOf("zh", "zh_TW", "en"), LangTools.buildLangList(Lang.ZH_TW))
    assertContentEquals(listOf("zh", "zh_CN", "en"), LangTools.buildLangList(Lang.ZH_CN))
    assertContentEquals(listOf("ja", "ja_JP", "zh", "en"), LangTools.buildLangList(Lang.of("ja-JP")!!))
    assertContentEquals(listOf("ko", "en", "zh"), LangTools.buildLangList(Lang.of("ko")!!))
    assertContentEquals(listOf("fr"), LangTools.buildLangList(Lang.of("fr")!!))
  }

  // ------------------------------------------------------- getString / getStringOrDefault

  private val localeMap = mapOf(
    Locale.TAIWAN to "繁中",
    Locale.CHINA to "简中",
    Locale.ENGLISH to "English",
    Locale.of("ja") to "日本語",
  )
  private val langMap = localeMap.mapKeys { (k, _) -> k.toLang() }

  @Test
  fun `getString 與 LocaleTools 一致`() {
    for (locale in comparable) {
      assertEquals(
        LocaleTools.getString(localeMap, locale),
        LangTools.getString(langMap, locale.toLang()),
        "getString of $locale"
      )
    }
  }

  @Test
  fun `getString 的實際行為`() {
    assertEquals("繁中", LangTools.getString(langMap, Lang.ZH_TW))
    assertEquals("English", LangTools.getString(langMap, Lang.EN))
    assertEquals("English", LangTools.getString(langMap, Lang.of("en-US")!!))  // 退到僅語言
    assertEquals("日本語", LangTools.getString(langMap, Lang.of("ja-JP")!!))
    assertNull(LangTools.getString(langMap, Lang.of("fr")!!))
    assertNull(LangTools.getString(langMap, Lang.ZH))                          // map 中無單獨 "zh"
  }

  @Test
  fun `getStringOrDefault 與 LocaleTools 一致`() {
    for (locale in comparable) {
      assertEquals(
        LocaleTools.getStringOrDefault(localeMap, locale),
        LangTools.getStringOrDefault(langMap, locale.toLang()),
        "getStringOrDefault of $locale"
      )
    }
  }

  @Test
  fun `getStringOrDefault 找不到時退回最佳匹配、再退回第一個`() {
    assertEquals("繁中", LangTools.getStringOrDefault(langMap, Lang.ZH))       // zh → 匹配到 zh-TW
    assertEquals("繁中", LangTools.getStringOrDefault(langMap, Lang.of("fr")!!)) // 全無 → 第一個
  }

  @Test
  fun `getStringOrDefault 空 map 會拋例外`() {
    assertFailsWith<NoSuchElementException> {
      LangTools.getStringOrDefault(emptyMap(), Lang.ZH_TW)
    }
  }

  // ------------------------------------------------------- getBestMatching

  @Test
  fun `getBestMatchingLang 與 LocaleTools 一致`() {
    val candidates = localeMap.keys
    val langCandidates = candidates.map { it.toLang() }
    for (locale in comparable) {
      assertEquals(
        LocaleTools.getBestMatchingLocale(locale, candidates)?.toLang(),
        LangTools.getBestMatchingLang(locale.toLang(), langCandidates),
        "getBestMatchingLang of $locale"
      )
      assertEquals(
        LocaleTools.getBestMatchingLocaleOrFirst(locale, candidates).toLang(),
        LangTools.getBestMatchingLangOrFirst(locale.toLang(), langCandidates),
        "getBestMatchingLangOrFirst of $locale"
      )
    }
  }

  @Test
  fun `getBestMatchingLang 的三層階梯`() {
    val candidates = listOf(Lang.ZH_TW, Lang.ZH_CN, Lang.EN, Lang.JA)
    assertEquals(Lang.ZH_TW, LangTools.getBestMatchingLang(Lang.ZH_TW, candidates))   // 語言＋地區
    assertEquals(Lang.ZH_TW, LangTools.getBestMatchingLang(Lang.ZH, candidates))      // 僅語言 → 首個 zh
    assertEquals(Lang.EN, LangTools.getBestMatchingLang(Lang.of("en-GB")!!, candidates))
    assertNull(LangTools.getBestMatchingLang(Lang.of("fr")!!, candidates))
  }

  /**
   * 第二層（語言＋地區）唯有在 **variant 不同** 時才與第一層有別 —— 兩者的判斷式
   * 只差在 variants 比對。若測試資料全無 variant，拿掉第二層也不會有任何測試失敗
   * （變異測試實證過）。故此處專門用帶 variant 的輸入把第二層釘住。
   */
  @Test
  fun `getBestMatchingLang 的第二層 —— variant 不符時退到語言＋地區`() {
    val candidates = listOf(Lang.of("en-GB")!!, Lang.of("en-US")!!)
    val target = Lang.of("en-US-posix")!!

    // 第一層不中（候選皆無 variant）；第二層以 en-US 命中。
    // 若少了第二層會掉到第三層（僅語言），錯誤地傳回 en-GB。
    assertEquals(Lang.of("en-US"), LangTools.getBestMatchingLang(target, candidates))
    assertNotEquals(Lang.of("en-GB"), LangTools.getBestMatchingLang(target, candidates))

    // 候選帶相同 variant 時，第一層即命中
    val withVariant = listOf(Lang.of("en-GB")!!, Lang.of("en-US-posix")!!)
    assertEquals(Lang.of("en-US-posix"), LangTools.getBestMatchingLang(target, withVariant))
  }

  @Test
  fun `getBestMatchingLangWithDefault 找不到時以 DEFAULT 再試一次`() {
    val candidates = listOf(Lang.ZH_TW, Lang.EN)
    assertEquals(Lang.EN, LangTools.getBestMatchingLangWithDefault(candidates, Lang.of("en-GB")!!))
    // fr 無匹配 → 退回 DEFAULT(zh-TW) 再比對
    assertEquals(Lang.ZH_TW, LangTools.getBestMatchingLangWithDefault(candidates, Lang.of("fr")!!))
    assertNull(LangTools.getBestMatchingLangWithDefault(listOf(Lang.JA), Lang.of("fr")!!))
  }

  // ------------------------------------------------------- 刻意不同之處

  /**
   * 以下三項是 [LangTools] 相對 [LocaleTools] 的**修正**，故意不一致。
   * 用測試釘住，避免日後被誤當成 bug「修」回去。
   */
  @Test
  fun `刻意的行為差異`() {
    // ① 三段輸入：Locale 版把 Hant 當成 country，產生壞掉的 Locale
    val brokenLocale = LocaleTools.getLocale("zh-Hant-TW")!!
    assertEquals("HANT", brokenLocale.country)
    assertEquals("TW", brokenLocale.variant)
    assertEquals("", brokenLocale.script)
    assertEquals("zh-x-lvariant-TW", brokenLocale.toLanguageTag())
    // Lang 版正確辨識 script
    Lang.of("zh-Hant-TW")!!.let {
      assertEquals("Hant", it.script)
      assertEquals("TW", it.region)
      assertEquals("zh-Hant-TW", it.tag)
    }
    assertNotEquals(brokenLocale.toLang(), Lang.of("zh-Hant-TW"))

    // ② 底線分隔：Java 只吃連字號
    assertEquals(Locale.ROOT, Locale.forLanguageTag("zh_TW"))
    assertEquals(Lang.ZH_TW, Lang.of("zh_TW"))

    // ③ Accept-Language "*"：Locale 版得到 ROOT（未定語言），Lang 版回傳 default
    assertEquals(Locale.ROOT, LocaleTools.parseAcceptLanguageHeader("*", Locale.TRADITIONAL_CHINESE))
    assertEquals(Lang.DEFAULT, LangTools.parseAcceptLanguageHeader("*"))
  }
}
