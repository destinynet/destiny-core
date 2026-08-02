/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

import java.nio.file.Paths
import java.util.Locale
import java.util.ResourceBundle
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LangTest {

  /** 本專案實際使用的語言（依 .properties 後綴 + Locale 常數用量） */
  private val inUse = listOf(
    Locale.TAIWAN,              // zh-TW，同 TRADITIONAL_CHINESE
    Locale.CHINA,               // zh-CN，同 SIMPLIFIED_CHINESE
    Locale.ENGLISH,             // en
    Locale.JAPAN,               // ja-JP
    Locale.of("ja"),            // ja
    Locale.of("zh"),            // zh
  )

  // ------------------------------------------------------------ 往返

  @Test
  fun `Locale 往返不失真`() {
    val all = inUse + listOf(
      Locale.US, Locale.UK, Locale.KOREA, Locale.ITALY, Locale.GERMANY,
      Locale.FRANCE, Locale.CANADA, Locale.ROOT,
      Locale.forLanguageTag("zh-Hant-TW"),
      Locale.forLanguageTag("zh-Hans-CN"),
    )
    for (locale in all) {
      val lang = locale.toLang()
      assertEquals(locale.toLanguageTag(), if (lang.isRoot) "und" else lang.tag, "tag of $locale")
      assertEquals(locale, lang.toLocale(), "roundtrip of $locale")
    }
  }

  @Test
  fun `Lang 往返不失真`() {
    for (tag in listOf("zh", "zh-TW", "zh-CN", "en", "en-US", "ja", "ja-JP", "zh-Hant-TW", "")) {
      val lang = assertNotNull(Lang.of(tag.ifEmpty { "und" }))
      assertEquals(tag, lang.tag, "tag")
      assertEquals(lang, lang.toLocale().toLang(), "roundtrip via Locale")
    }
  }

  // ------------------------------------------------------------ 正規化

  @Test
  fun `正規化 —— 分隔符號、大小寫、空白皆等價`() {
    val expected = Lang.ZH_TW
    for (raw in listOf("zh-TW", "zh_TW", "zh-tw", "ZH_TW", "Zh-Tw", "  zh-TW  ", "zh_TW_#Hant")) {
      assertEquals(expected, Lang.of(raw), "of(\"$raw\")")
    }
  }

  @Test
  fun `正規化 —— script 首字大寫、地區大寫`() {
    assertEquals("zh-Hant-TW", Lang.of("ZH-HANT-tw")?.tag)
    assertEquals("zh-Hans-CN", Lang.of("zh_hans_cn")?.tag)
    assertEquals("es-419", Lang.of("ES-419")?.tag)          // 3 碼數字地區
  }

  /**
   * 正規化的目的就是這個 —— [Lang] 的相等性是 String 相等性，
   * 若不強制正規化，同一語言的不同寫法會變成不同的 Map key。
   */
  @Test
  fun `正規化 —— 可安全作為 Map key`() {
    val map = mapOf(Lang.ZH_TW to "繁中", Lang.EN to "English")
    assertEquals("繁中", map[Lang.of("zh_tw")])
    assertEquals("繁中", map[Lang.of("ZH-TW")])
    assertEquals("English", map[Lang.of("EN")])
    assertEquals(1, setOf(Lang.of("zh-TW"), Lang.of("zh_tw"), Lang.of("ZH-Tw")).size)
  }

  @Test
  fun `無法解析者回傳 null`() {
    for (raw in listOf(null, "", "   ", "-", "_", "#", "1", "12345678901", "zh1")) {
      assertNull(Lang.of(raw), "of(${raw?.let { "\"$it\"" }})")
    }
  }

  @Test
  fun `und 視為 ROOT`() {
    assertEquals(Lang.ROOT, Lang.of("und"))
    assertEquals(Lang.ROOT, Lang.of("UND"))
    assertTrue(Lang.ROOT.isRoot)
    assertEquals("", Lang.ROOT.tag)
  }

  // ------------------------------------------------------------ 欄位存取

  @Test
  fun `subtag 存取`() {
    Lang.of("zh-Hant-TW")!!.let {
      assertEquals("zh", it.language)
      assertEquals("Hant", it.script)
      assertEquals("TW", it.region)
      assertContentEquals(emptyList(), it.variants)
    }
    Lang.ZH_TW.let {
      assertEquals("zh", it.language)
      assertNull(it.script)
      assertEquals("TW", it.region)
    }
    Lang.EN.let {
      assertEquals("en", it.language)
      assertNull(it.script)
      assertNull(it.region)
    }
    Lang.of("en-US-posix")!!.let {
      assertEquals("en", it.language)
      assertNull(it.script)
      assertEquals("US", it.region)
      assertContentEquals(listOf("posix"), it.variants)
    }
    Lang.ROOT.let {
      assertEquals("", it.language)
      assertNull(it.script)
      assertNull(it.region)
    }
  }

  /** 與 [Locale] 的對應欄位一致 */
  @Test
  fun `subtag 與 Locale 一致`() {
    for (locale in inUse + Locale.forLanguageTag("zh-Hant-TW")) {
      val lang = locale.toLang()
      assertEquals(locale.language, lang.language, "language of $locale")
      assertEquals(locale.country.ifEmpty { null }, lang.region, "region of $locale")
      assertEquals(locale.script.ifEmpty { null }, lang.script, "script of $locale")
    }
  }

  // ------------------------------------------------------------ 資源查找

  @Test
  fun `resourceSuffix 對得上實際的 properties 檔名`() {
    assertEquals("_zh_TW", Lang.ZH_TW.resourceSuffix())
    assertEquals("_zh_CN", Lang.ZH_CN.resourceSuffix())
    assertEquals("_en", Lang.EN.resourceSuffix())
    assertEquals("_ja", Lang.JA.resourceSuffix())
    assertEquals("_zh", Lang.ZH.resourceSuffix())
    assertEquals("", Lang.ROOT.resourceSuffix())

    // 拿真實存在的 bundle 驗一次，避免只是自說自話
    val base = "src/main/resources/destiny/core/EventCategory"
    assertTrue(Paths.get("$base.properties").exists(), "基準檔應存在")
    for (lang in listOf(Lang.EN, Lang.ZH_CN, Lang.JA)) {
      assertTrue(
        Paths.get("$base${lang.resourceSuffix()}.properties").exists(),
        "EventCategory${lang.resourceSuffix()}.properties 應存在"
      )
    }
    // _zh_TW 形式（兩段後綴）另找一個 bundle 驗
    assertTrue(
      Paths.get("src/main/resources/destiny/core/astrology/ZodiacSign${Lang.ZH_TW.resourceSuffix()}.properties").exists(),
      "ZodiacSign_zh_TW.properties 應存在"
    )
  }

  @Test
  fun `fallbacks 由精確到寬鬆`() {
    assertContentEquals(listOf(Lang.ZH_TW, Lang.ZH, Lang.ROOT), Lang.ZH_TW.fallbacks())
    assertContentEquals(listOf(Lang.EN, Lang.ROOT), Lang.EN.fallbacks())
    assertContentEquals(
      listOf(Lang.of("zh-Hant-TW"), Lang.of("zh-Hant"), Lang.ZH, Lang.ROOT),
      Lang.of("zh-Hant-TW")!!.fallbacks()
    )
    assertContentEquals(listOf(Lang.ROOT), Lang.ROOT.fallbacks())
  }

  /**
   * [Lang.fallbacks] 必須與 Java `ResourceBundle` 的實際解析結果一致。
   *
   * ResourceBundle 會替中文自動補上隱含 script
   * （`zh_TW` → `[zh_TW_#Hant, zh__#Hant, zh_TW, zh, ROOT]`），
   * 但本 repo 沒有任何帶 script 的 .properties，那些候選永遠落空 ——
   * 故此處比對「濾掉帶 script 的候選」之後的序列。
   */
  @Test
  fun `fallbacks 對齊 ResourceBundle 的有效候選序`() {
    val control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT)
    for (locale in inUse) {
      val jdkEffective = control.getCandidateLocales("x", locale)
        .filter { it.script.isEmpty() }
        .map { it.toLang() }
      assertContentEquals(jdkEffective, locale.toLang().fallbacks(), "fallbacks of $locale")
    }
  }

  // ------------------------------------------------------------ 常數

  @Test
  fun `常數對應 Locale 常數`() {
    assertEquals(Locale.TAIWAN.toLang(), Lang.ZH_TW)
    assertEquals(Locale.TRADITIONAL_CHINESE.toLang(), Lang.ZH_TW)   // JDK 中兩者是同一個物件
    assertEquals(Locale.CHINA.toLang(), Lang.ZH_CN)
    assertEquals(Locale.SIMPLIFIED_CHINESE.toLang(), Lang.ZH_CN)
    assertEquals(Locale.CHINESE.toLang(), Lang.ZH)
    assertEquals(Locale.ENGLISH.toLang(), Lang.EN)
    assertEquals(Locale.ROOT.toLang(), Lang.ROOT)
    assertEquals(Lang.ZH_TW, Lang.DEFAULT)                          // 專案預設為 zh_TW
  }
}
