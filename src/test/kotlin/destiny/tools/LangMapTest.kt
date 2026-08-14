/**
 * Created by smallufo on 2026-08-14.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * [byLang] —— 以語系鍵查表的容忍度與精確度順序。
 *
 * 存在的理由：以字串為鍵的多語系 map（JSONB、設定檔、外部 API 回應）在真實資料裡
 * 常常同時存在多種寫法（`zh-TW` / `zh_TW` / `ZH-tw`），而 `Map` 的相等性是字面相等。
 * 少一層正規化的症狀是「查不到、退回預設語言」—— 沒有例外、沒有錯誤訊息。
 */
class LangMapTest {

  private val zhTw = Lang.of("zh-TW")!!
  private val zhCn = Lang.of("zh-CN")!!
  private val en = Lang.of("en")!!

  @Test
  fun canonicalKey() {
    assertEquals("諾貝爾獎", mapOf("en" to "Nobel", "zh-TW" to "諾貝爾獎").byLang(zhTw))
  }

  /** 底線是 legacy 寫法（`Locale.toString()`），仍要查得到 */
  @Test
  fun underscoreKey() {
    assertEquals("諾貝爾獎", mapOf("en" to "Nobel", "zh_TW" to "諾貝爾獎").byLang(zhTw))
  }

  @Test
  fun sloppyCaseKey() {
    assertEquals("諾貝爾獎", mapOf("ZH_tw" to "諾貝爾獎").byLang(zhTw))
  }

  /** 沒有完整標籤時退一階到語言碼 */
  @Test
  fun fallsBackToLanguage() {
    assertEquals("诺贝尔奖", mapOf("en" to "Nobel", "zh" to "诺贝尔奖").byLang(zhTw))
  }

  /**
   * **精確度優先於鍵格式**：完整標籤即使寫成 legacy 底線，也要贏過正規形的語言碼。
   * 順序寫反的症狀最陰險 —— 簡繁混淆而非查不到。
   */
  @Test
  fun exactTagBeatsLanguageEvenWhenLegacyWritten() {
    assertEquals("繁體", mapOf("zh" to "通用", "zh_TW" to "繁體").byLang(zhTw))
  }

  /** 地區不同不得互相命中：zh-CN 不該拿到 zh-TW 的值 */
  @Test
  fun regionIsNotInterchangeable() {
    assertEquals("简体", mapOf("zh-TW" to "繁體", "zh-CN" to "简体").byLang(zhCn))
  }

  /** 查不到就是 null —— 後備策略（退英文？退原名？）由呼叫端決定 */
  @Test
  fun missReturnsNull() {
    assertNull(mapOf("en" to "Nobel", "ja" to "ノーベル賞").byLang(zhTw))
  }

  @Test
  fun emptyMapReturnsNull() {
    assertNull(emptyMap<String, String>().byLang(zhTw))
  }

  /** 無法解析的鍵不得讓整批查詢爆掉 */
  @Test
  fun unparsableKeysAreIgnored() {
    assertEquals("諾貝爾獎", mapOf("???" to "垃圾", "" to "空", "zh-TW" to "諾貝爾獎").byLang(zhTw))
  }

  /** 泛型：值不限於 String */
  @Test
  fun worksWithNonStringValues() {
    assertEquals(listOf(1, 2), mapOf("zh_TW" to listOf(1, 2)).byLang(zhTw))
  }

  @Test
  fun englishIsJustAnotherLang() {
    assertEquals("Nobel", mapOf("en" to "Nobel", "zh-TW" to "諾貝爾獎").byLang(en))
  }
}
