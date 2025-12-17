/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.tools

import java.util.*
import kotlin.test.*

class LocaleToolsTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun getLocale() {

    assertNull(LocaleTools.getLocale(""))

    assertEquals(Locale.of("zh"), LocaleTools.getLocale("zh"))
    assertEquals(Locale.of("zh"), LocaleTools.getLocale("ZH"))
    assertEquals(Locale.of("zh"), LocaleTools.getLocale("ZH#TAIWAN"))

    assertEquals(Locale.TAIWAN, LocaleTools.getLocale("zh_TW"))
    assertEquals(Locale.TAIWAN, LocaleTools.getLocale("zh_TW#TAIPEI"))
    assertEquals(Locale.CHINA, LocaleTools.getLocale("zh_CN"))

    assertEquals(Locale.of("zh" , "TW" , "TAIPEI"), LocaleTools.getLocale("zh_TW_TAIPEI"))
    assertEquals(Locale.of("zh" , "TW" , "TAIPEI"), LocaleTools.getLocale("zh_TW_TAIPEI#ABC"))

    assertEquals(Locale.of("zh" , "HK"), LocaleTools.getLocale("zh_HK_#Hant"))
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
