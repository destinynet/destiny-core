/**
 * Created by smallufo on 2021-07-19.
 */
package destiny.tools.serializers

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LocaleSerializerTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testEncodeDecode() {
    val module = SerializersModule {
      contextual(LocaleSerializer)
    }

    val format = Json { serializersModule = module }

    format.encodeToString(Locale.TAIWAN).also { raw ->
      assertEquals(""""zh-TW"""", raw)

      assertEquals(Locale.TAIWAN, format.decodeFromString<Locale>(raw))
    }
  }

  /**
   * 寫入端一律 BCP-47（`zh-TW`），但讀取端必須**額外**容忍 legacy 的底線形式。
   *
   * `Locale.forLanguageTag("zh_TW")` 不丟例外，而是靜默回傳 `Locale.ROOT` ——
   * 與 `LocaleAttributeConverter` 曾發生過的是同一個 bug。
   */
  @Test
  fun `deserialize 額外支援 legacy 底線形式`() {
    val format = Json { serializersModule = SerializersModule { contextual(LocaleSerializer) } }

    assertEquals(Locale.TAIWAN, format.decodeFromString<Locale>(""""zh_TW""""))
    assertEquals(Locale.SIMPLIFIED_CHINESE, format.decodeFromString<Locale>(""""zh_CN""""))
    assertEquals(Locale.US, format.decodeFromString<Locale>(""""en_US""""))
    // script：Locale.of("zh","HANT") 那條路徑產生的 legacy 值
    assertEquals(Locale.forLanguageTag("zh-Hant"), format.decodeFromString<Locale>(""""zh_HANT""""))
    // BCP-47 與 ROOT 行為不變
    assertEquals(Locale.TAIWAN, format.decodeFromString<Locale>(""""zh-TW""""))
    assertEquals(Locale.ROOT, format.decodeFromString<Locale>(""""und""""))
  }

  /**
   * prod 的 `data.category.names` 共 865 筆，每筆都同時帶 `zh_TW` 與 `zh_CN` 兩個 key。
   * 舊實作把兩者都解析成 `Locale.ROOT`，**後者覆蓋前者** —— 簡中譯名靜默消失，
   * 且倖存的繁中掛在 ROOT 上，用 `Locale.TAIWAN` 查不到。
   */
  @Test
  fun `LocaleMapSerializer 讀取 legacy 底線 key 不得碰撞`() {
    val raw = """{"en":"Vocation","ja":"職業","ko":"직업","zh_CN":"职业","zh_TW":"職業"}"""

    val map = Json.decodeFromString(LocaleMapSerializer, raw)

    assertEquals(5, map.size)
    assertEquals("Vocation", map[Locale.ENGLISH])
    assertEquals("職業", map[Locale.JAPANESE])
    assertEquals("직업", map[Locale.KOREAN])
    assertEquals("职业", map[Locale.SIMPLIFIED_CHINESE])
    assertEquals("職業", map[Locale.TAIWAN])
    assertNull(map[Locale.ROOT])
  }

  /** 寫入端本來就正確，這裡釘住它不會被一起改壞 */
  @Test
  fun `LocaleMapSerializer 寫入一律 BCP-47`() {
    val map = mapOf(Locale.ENGLISH to "Vocation", Locale.TAIWAN to "職業", Locale.SIMPLIFIED_CHINESE to "职业")

    assertEquals("""{"en":"Vocation","zh-TW":"職業","zh-CN":"职业"}""", Json.encodeToString(LocaleMapSerializer, map))
  }
}
