/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.tools

import java.util.*
import kotlin.test.*

class LocaleToolsTest {

  val logger = KotlinLogging.logger {  }

  // ========== parseAcceptLanguageHeader tests ==========

  @Test
  fun `parseAcceptLanguageHeader with null`() {
    assertEquals(Locale.TRADITIONAL_CHINESE, LocaleTools.parseAcceptLanguageHeader(null))
  }

  @Test
  fun `parseAcceptLanguageHeader with blank`() {
    assertEquals(Locale.TRADITIONAL_CHINESE, LocaleTools.parseAcceptLanguageHeader(""))
    assertEquals(Locale.TRADITIONAL_CHINESE, LocaleTools.parseAcceptLanguageHeader("  "))
  }

  @Test
  fun `parseAcceptLanguageHeader with simple locale`() {
    assertEquals(Locale.of("en"), LocaleTools.parseAcceptLanguageHeader("en"))
  }

  @Test
  fun `parseAcceptLanguageHeader with quality factors`() {
    assertEquals(Locale.of("zh", "TW"), LocaleTools.parseAcceptLanguageHeader("zh-TW,zh;q=0.9,en;q=0.8"))
  }

  @Test
  fun `parseAcceptLanguageHeader with custom default`() {
    assertEquals(Locale.ENGLISH, LocaleTools.parseAcceptLanguageHeader(null, Locale.ENGLISH))
    assertEquals(Locale.ENGLISH, LocaleTools.parseAcceptLanguageHeader("", Locale.ENGLISH))
  }

  // ========== buildLocaleList tests ==========

  @Test
  fun `buildLocaleList with english falls back to zh`() {
    assertEquals(listOf("en", "zh"), LocaleTools.buildLocaleList(Locale.of("en")))
  }

  @Test
  fun `buildLocaleList with zh_TW includes country variant then en`() {
    assertEquals(listOf("zh", "zh_TW", "en"), LocaleTools.buildLocaleList(Locale.of("zh", "TW")))
  }

  @Test
  fun `buildLocaleList with japanese falls back to zh then en`() {
    assertEquals(listOf("ja", "ja_JP", "zh", "en"), LocaleTools.buildLocaleList(Locale.of("ja", "JP")))
  }

  @Test
  fun `buildLocaleList with korean falls back to en then zh`() {
    assertEquals(listOf("ko", "en", "zh"), LocaleTools.buildLocaleList(Locale.of("ko")))
  }

  @Test
  fun `buildLocaleList with unknown language has no fallback`() {
    assertEquals(listOf("fr"), LocaleTools.buildLocaleList(Locale.of("fr")))
  }

  // ========== getLocale tests ==========

  /** 常見輸入 —— 2026-08-03 改委派 [destiny.tools.Lang.of] 後行為完全不變 */
  @Test
  fun getLocale() {

    assertNull(LocaleTools.getLocale(""))
    assertNull(LocaleTools.getLocale("   "))
    assertNull(LocaleTools.getLocale(null))

    assertEquals(Locale.of("zh"), LocaleTools.getLocale("zh"))
    assertEquals(Locale.of("zh"), LocaleTools.getLocale("ZH"))
    assertEquals(Locale.of("zh"), LocaleTools.getLocale("ZH#TAIWAN"))

    assertEquals(Locale.TAIWAN, LocaleTools.getLocale("zh_TW"))
    assertEquals(Locale.TAIWAN, LocaleTools.getLocale("zh-TW"))
    assertEquals(Locale.TAIWAN, LocaleTools.getLocale("zh_TW#TAIPEI"))
    assertEquals(Locale.CHINA, LocaleTools.getLocale("zh_CN"))

    assertEquals(Locale.of("zh", "HK"), LocaleTools.getLocale("zh_HK_#Hant"))

    assertEquals(Locale.of("en", "US"), LocaleTools.getLocale("en_US"))
    assertEquals(Locale.of("ja"), LocaleTools.getLocale("ja"))
    assertEquals(Locale.of("ko", "KR"), LocaleTools.getLocale("ko-KR"))
  }

  /**
   * **2026-08-03 的 bug 修正**：舊版按位置切分成 language/country/variant，
   * 把 BCP-47 合法的 `zh-Hant-TW`（CLDR / Android / iOS 對繁中的正規寫法）
   * 誤解成 `country=HANT, variant=TW`，language tag 變成 `zh-x-lvariant-TW`，
   * 導致後續 ResourceBundle 查找全數落空。
   *
   * 而 `AbstractLineService` 會把此結果經 `destinyUserDao.getOrCreate()` 寫進 DB。
   */
  @Test
  fun `getLocale 正確辨識 script 子標籤`() {
    val locale = LocaleTools.getLocale("zh-Hant-TW")!!
    assertEquals("zh", locale.language)
    assertEquals("Hant", locale.script)
    assertEquals("TW", locale.country)
    assertEquals("", locale.variant)
    assertEquals("zh-Hant-TW", locale.toLanguageTag())

    // 底線寫法亦同
    assertEquals(locale, LocaleTools.getLocale("zh_Hant_TW"))
    // 簡體
    assertEquals("zh-Hans-CN", LocaleTools.getLocale("zh-Hans-CN")!!.toLanguageTag())
  }

  /** 修正的附帶差異 —— 皆為改善，逐項釘住免得日後被誤「修」回去 */
  @Test
  fun `getLocale 修正後的附帶差異`() {
    // ① variant 正規化為小寫（BCP-47 標準大小寫）
    val v = LocaleTools.getLocale("zh_TW_TAIPEI")!!
    assertEquals("taipei", v.variant)
    assertEquals(Locale.forLanguageTag("zh-TW-taipei"), v)
    assertEquals(v, LocaleTools.getLocale("zh_TW_TAIPEI#ABC"))

    // ② 非法語言碼回傳 null，而非產生 tag 為 "und" 的畸形 Locale
    //    （所有呼叫端本來就處理 null —— 回傳型別一向可空）
    assertNull(LocaleTools.getLocale("1"))
    assertNull(LocaleTools.getLocale("zh1"))
    assertNull(LocaleTools.getLocale("-"))

    // ③ "und" 是「語言未定」，應為 ROOT 而非 language=="und" 的 Locale
    assertEquals(Locale.ROOT, LocaleTools.getLocale("und"))
  }

  @Test
  fun `完全符合，包含大小寫相異`() {
    val locales = listOf(
      Locale.of("zh", "TW", "AAA"),
      Locale.of("zh", "TW", "BBB"),
      Locale.of("zh", "TW", "CCC")
    )

    // 完全符合
    val locale1 = Locale.of("zh", "TW", "AAA")
    assertEquals(Locale.of("zh", "TW", "AAA"), LocaleTools.getBestMatchingLocale(locale1, locales))

    // 完全符合 , 大小寫不同 視為符合
    val locale2 = Locale.of("zh", "TW", "aaa")
    assertEquals(Locale.of("zh", "TW", "AAA"), LocaleTools.getBestMatchingLocale(locale2, locales))
  }

  @Test
  fun `語言、國家符合`() {
    val locales = listOf(
      Locale.of("zh", "TW", "AAA"),
      Locale.of("zh", "TW", "BBB"),
      Locale.of("zh", "TW", "CCC")
    )

    //只有 語言/國家 符合，不知道傳回來的是哪一個，總之不為空即可
    val locale1 = Locale.of("zh", "TW")
    assertNotNull(LocaleTools.getBestMatchingLocale(locale1, locales))

    val locale2 = Locale.of("zh", "tw")
    assertNotNull(LocaleTools.getBestMatchingLocale(locale2, locales))
  }

  @Test
  fun 語言符合() {
    val locales = listOf(
      Locale.of("zh", "TW", "AAA"),
      Locale.of("zh", "TW", "BBB"),
      Locale.of("zh", "TW", "CCC")
    )

    //只有 語言 符合，不知道傳回來的是哪一個，總之不為空即可
    val locale1 = Locale.of("zh")
    assertNotNull(LocaleTools.getBestMatchingLocale(locale1, locales))

    val locale2 = Locale.of("ZH")
    assertNotNull(LocaleTools.getBestMatchingLocale(locale2, locales))
  }

  @Test
  fun `中文找英文,找不到`() {
    val locales = listOf(
      Locale.of("en", "US", "AAA"),
      Locale.of("en", "US", "BBB"),
      Locale.of("en", "US", "CCC")
    )

    val locale = Locale.of("zh", "TW")
    assertNull(LocaleTools.getBestMatchingLocale(locale, locales))
  }


  @Test
  fun `英文找中文,找不到`() {
    val locales = listOf(
      Locale.of("zh", "TW", "AAA"),
      Locale.of("zh", "TW", "BBB"),
      Locale.of("zh", "TW", "CCC")
    )

    val locale = Locale.of("en", "US")
    assertNull(LocaleTools.getBestMatchingLocale(locale, locales))
  }



  @Test
  fun `提供正體中文，抓取正體中文`() {
    val langMap = mapOf(
      Locale.of("zh", "TW") to "父親"
    )

    assertEquals("父親", LocaleTools.getString(langMap, Locale.of("zh", "TW")))
  }

  @Test
  fun `提供正體、簡體中文`() {
    val langMap = mapOf(
      Locale.of("zh", "TW") to "父親",
      Locale.of("zh", "CN") to "父亲"
    )

    assertEquals("父親", LocaleTools.getString(langMap, Locale.of("zh", "TW", "AAA")))
    assertEquals("父親", LocaleTools.getString(langMap, Locale.of("zh", "TW")))
    assertEquals("父亲", LocaleTools.getString(langMap, Locale.of("zh", "CN")))
  }

  @Test
  fun `提供正體中文，抓取簡體中文 必須要能抓到`() {
    val langMap = mapOf(
      Locale.of("zh", "TW") to "父親"
    )

    assertEquals("父親", LocaleTools.getStringOrDefault(langMap, Locale.of("zh", "CN")))
  }

  /**
   * langMap 為 ("ja_JP") , 目標語言為 "ja"
   *
   * 第一步，先把目標語言 "ja" 展開三層 -> "ja" , "ja" , "ja"
   * 再從 langMap 找，沒有 key符合的，傳回 null
   *
   *
   * 再呼叫 [LocaleTools.getBestMatchingLocale] , 從 "ja_jp" 中尋找 "ja" , 找到 符合了 , 傳回其值
   */
  @Test
  fun `從 ja_JP 尋找 ja 符合的字串(步驟最多)`() {
    val langMap = mapOf(
      Locale.ENGLISH to "Father"  // "en"
      , Locale.JAPAN to "お父さん"  //  "ja","JP"
    )

    assertEquals("お父さん", LocaleTools.getStringOrDefault(langMap, Locale.JAPANESE))  // "ja"
    assertEquals("Father", LocaleTools.getStringOrDefault(langMap, Locale.ENGLISH))  // en
    assertEquals("Father", LocaleTools.getStringOrDefault(langMap, Locale.TAIWAN))  // 內定找到 map 第一個
  }


  @Test
  fun getStringOrDefault2() {
    val langMap = mapOf(
      Locale.of("zh" ,"TW" , "general") to "父親" ,
      Locale.of("zh" ,"TW" , "taiwan") to "阿爸" ,
      Locale.of("zh" ,"CN" ) to "父亲"
    )

    assertTrue { LocaleTools.getStringOrDefault(langMap , Locale.of("zh" , "TW")) in arrayOf("父親", "阿爸") }

    assertTrue { LocaleTools.getStringOrDefault(langMap , Locale.of("zh")) in arrayOf("父親", "阿爸", "父亲") }

    assertEquals("父親" , LocaleTools.getStringOrDefault(langMap , Locale.of("zh" , "TW" , "general")))
    assertEquals("阿爸" , LocaleTools.getStringOrDefault(langMap , Locale.of("zh" , "TW" , "taiwan")))
    assertEquals("父亲" , LocaleTools.getStringOrDefault(langMap , Locale.of("zh" , "CN")))
    assertEquals("父亲" , LocaleTools.getStringOrDefault(langMap , Locale.of("zh" , "CN" , "Beijing")))
  }

}
