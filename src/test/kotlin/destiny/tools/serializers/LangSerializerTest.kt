/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools.serializers

import destiny.tools.Lang
import destiny.tools.toLang
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [LangSerializer] / [LangMapSerializer] 產出的 JSON 必須與
 * [LocaleSerializer] / [LocaleMapSerializer] **逐字相同**。
 *
 * 這是 [Lang] 能與 `java.util.Locale` 長期並存、DTO 逐個遷移的前提：
 *  - `LocaleSerializer` 有 10 個使用點，橫跨 6 個 repo
 *  - `LocaleMapSerializer` 被 destiny-jakarta-ee 的 `LocaleMapConverter`
 *    （JPA `AttributeConverter`）用來讀寫 DB 的 JSONB 欄位 —— 格式一旦不同，
 *    既有資料就讀不回來
 */
class LangSerializerTest {

  @Serializable
  private data class LocaleHolder(@Serializable(with = LocaleSerializer::class) val v: Locale)

  @Serializable
  private data class LangHolder(@Serializable(with = LangSerializer::class) val v: Lang)

  /** Lang 自帶 @Serializable(with = LangSerializer)，此處驗證免標註也能用 */
  @Serializable
  private data class BareLangHolder(val v: Lang)

  @Serializable
  private data class LocaleMapHolder(@Serializable(with = LocaleMapSerializer::class) val m: Map<Locale, String>)

  @Serializable
  private data class LangMapHolder(@Serializable(with = LangMapSerializer::class) val m: Map<Lang, String>)

  private val json = Json

  private val locales = listOf(
    Locale.TAIWAN, Locale.CHINA, Locale.ENGLISH, Locale.US, Locale.UK,
    Locale.JAPAN, Locale.of("ja"), Locale.of("zh"), Locale.KOREA,
    Locale.FRANCE, Locale.GERMANY, Locale.ITALY, Locale.ROOT,
    Locale.forLanguageTag("zh-Hant-TW"), Locale.forLanguageTag("zh-Hans-CN"),
  )

  // ------------------------------------------------------------ 單值

  @Test
  fun `LangSerializer 的 JSON 與 LocaleSerializer 逐字相同`() {
    for (locale in locales) {
      assertEquals(
        json.encodeToString(LocaleHolder(locale)),
        json.encodeToString(LangHolder(locale.toLang())),
        "encode $locale"
      )
    }
  }

  @Test
  fun `LangSerializer 往返`() {
    for (locale in locales) {
      val lang = locale.toLang()
      val encoded = json.encodeToString(LangHolder(lang))
      assertEquals(lang, json.decodeFromString<LangHolder>(encoded).v, "roundtrip $locale")
    }
  }

  @Test
  fun `既有的 Locale JSON 可被 LangSerializer 讀回`() {
    for (locale in locales) {
      val legacy = json.encodeToString(LocaleHolder(locale))
      assertEquals(locale.toLang(), json.decodeFromString<LangHolder>(legacy).v, "decode legacy $locale")
    }
  }

  @Test
  fun `Lang 免標註即可序列化`() {
    assertEquals("""{"v":"zh-TW"}""", json.encodeToString(BareLangHolder(Lang.ZH_TW)))
    assertEquals(Lang.ZH_TW, json.decodeFromString<BareLangHolder>("""{"v":"zh-TW"}""").v)
  }

  @Test
  fun `ROOT 編碼為 und`() {
    assertEquals("""{"v":"und"}""", json.encodeToString(LangHolder(Lang.ROOT)))
    assertEquals("""{"v":"und"}""", json.encodeToString(LocaleHolder(Locale.ROOT)))
    assertEquals(Lang.ROOT, json.decodeFromString<LangHolder>("""{"v":"und"}""").v)
  }

  @Test
  fun `無法解析者退回 ROOT —— 與 forLanguageTag 一致`() {
    for (garbage in listOf("", "!!!", "123")) {
      assertEquals(Locale.ROOT, Locale.forLanguageTag(garbage), "Locale 對 \"$garbage\"")
      assertEquals(Lang.ROOT, json.decodeFromString<LangHolder>("""{"v":"$garbage"}""").v, "Lang 對 \"$garbage\"")
    }
  }

  /**
   * [Lang.of] 比 `Locale.forLanguageTag` 寬鬆：吃底線分隔。
   * 這是刻意的修正 —— 舊資料裡若存在 `"zh_TW"` 這種 key，Locale 版會讀成 ROOT
   * （語言遺失），Lang 版能正確還原。
   */
  @Test
  fun `底線形式的舊資料 —— Lang 讀得回來，Locale 讀不回`() {
    assertEquals(Locale.ROOT, json.decodeFromString<LocaleHolder>("""{"v":"zh_TW"}""").v)
    assertEquals(Lang.ZH_TW, json.decodeFromString<LangHolder>("""{"v":"zh_TW"}""").v)
  }

  // ------------------------------------------------------------ Map

  @Test
  fun `LangMapSerializer 的 JSON 與 LocaleMapSerializer 逐字相同`() {
    val localeMap = mapOf(
      Locale.ENGLISH to "Vocation",
      Locale.TAIWAN to "職業",
      Locale.CHINA to "职业",
      Locale.of("ja") to "職業",
    )
    assertEquals(
      json.encodeToString(LocaleMapHolder(localeMap)),
      json.encodeToString(LangMapHolder(localeMap.mapKeys { (k, _) -> k.toLang() })),
    )
  }

  @Test
  fun `LangMapSerializer 往返`() {
    val langMap = mapOf(Lang.EN to "Vocation", Lang.ZH_TW to "職業", Lang.JA to "職業")
    val encoded = json.encodeToString(LangMapHolder(langMap))
    assertEquals("""{"m":{"en":"Vocation","zh-TW":"職業","ja":"職業"}}""", encoded)
    assertEquals(langMap, json.decodeFromString<LangMapHolder>(encoded).m)
  }

  @Test
  fun `既有的 LocaleMap JSON 可被 LangMapSerializer 讀回`() {
    val localeMap = mapOf(Locale.ENGLISH to "Vocation", Locale.TAIWAN to "職業")
    val legacy = json.encodeToString(LocaleMapHolder(localeMap))
    assertEquals(
      localeMap.mapKeys { (k, _) -> k.toLang() },
      json.decodeFromString<LangMapHolder>(legacy).m,
    )
  }
}
