/**
 * Created by smallufo on 2026-08-05.
 */
package destiny.tools

import destiny.core.astrology.Planet
import destiny.core.astrology.Star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * [I18nBundles] 是 destiny-core 內唯一碰 `ResourceBundle` 的地方。
 *
 * 測試資料用 `destiny.core.astrology.Star` bundle —— 注意它的 **base 是英文**
 * （`Star.properties` 的 `Planet.MARS = Mars`），中文在 `Star_zh.properties`。
 * 本 repo 的 bundle 並非一律以繁中為 base，寫測試時別假設。
 */
class I18nBundlesTest {

  private val starBundle = "destiny.core.astrology.Star"

  @Test
  fun `查得到就回傳字串`() {
    assertEquals("火星", I18nBundles.string(starBundle, Lang.ZH_TW, "Planet.MARS"))
    assertEquals("Mars", I18nBundles.string(starBundle, Lang.EN, "Planet.MARS"))
  }

  /**
   * 找不到 key 時回傳 null，**不拋** `MissingResourceException` ——
   * 這是接縫與 `ResourceBundle` 的刻意差異，讓呼叫端用 `?:` 兜底，
   * 也讓日後的 commonMain 實作不必模擬 JVM 的例外型別。
   */
  @Test
  fun `key 不存在回傳 null`() {
    assertNull(I18nBundles.string(starBundle, Lang.ZH_TW, "NO_SUCH_KEY"))
  }

  @Test
  fun `bundle 不存在回傳 null`() {
    assertNull(I18nBundles.string("no.such.Bundle", Lang.ZH_TW, "Planet.MARS"))
  }

  /** bundle 名稱推導的接縫 —— 未來進 commonMain 時，只有這一行要面對平台差異 */
  @Test
  fun `bundleName 與 java name 相同`() {
    assertEquals("destiny.core.astrology.Star", Star::class.bundleName())
    assertEquals("destiny.core.astrology.Planet", Planet::class.bundleName())
  }

  /**
   * 釘住 `ResourceBundle` 的 fallback 語意，**包含它會先退到 `Locale.getDefault()`**
   * 再退到 base bundle 這件事。
   *
   * 本專案的預設語系是 zh_TW（見 CLAUDE.md），因此查一個沒有對應 .properties 的語系（`ko`）
   * 會退到 `Star_zh.properties` 得到「火星」，而**不是** base 的 "Mars"。
   *
   * 這條之所以要釘住：日後 body 換成 codegen 查表時，這個「隱式依賴行程預設語系」的行為
   * **不會自動存在**，必須明確決定要複製它還是移除它。若移除，這個測試會失敗 —— 那是刻意的提醒。
   */
  @Test
  fun `未知語系會先退到行程預設語系而非 base`() {
    assertEquals("火星", I18nBundles.string(starBundle, Lang.of("ko")!!, "Planet.MARS"))
  }
}
