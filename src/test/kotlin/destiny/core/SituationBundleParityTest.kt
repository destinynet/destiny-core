/**
 * Created by Claude on 2026-09-02.
 */
package destiny.core

import destiny.tools.I18nBundles
import destiny.tools.Lang
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * [Situation] 的各語系 bundle 必須同步：key 集合一致、無空值。
 *
 * ## 為什麼 [I18nBundlesTest] 擋不住這件事
 *
 * 那條測試釘的是 `ResourceBundle` 的 **fallback 語意**，而 fallback 正是這裡的病因 ——
 * `I18nBundles` 的 KDoc 自己寫著「本專案預設 zh_TW，所以查 `ko` 會得到 `_zh` 的內容」。
 * 於是漏一個 key 的懲罰不是例外、不是 log，是**韓文站上長出一行繁體中文**，
 * 而且要有人看到那一頁、又剛好看得懂兩種語言，才會發現。
 *
 * 這不是假設：本 enum 的前身在 2026-08-22 補上韓文 bundle 之前，那個檔案根本不存在，
 * 韓文站的事件名**全部**是繁中，一路綠燈。本 enum 一次要生五個新檔案，
 * 犯同一個錯的機會比當初還多，所以測試先於 bundle 寫。
 *
 * ## 為什麼直接讀 .properties 而不透過 [I18nBundles]
 *
 * 走 `I18nBundles` 就會吃到 fallback，缺 key 也查得到值 —— 測試會全綠，
 * 而它要抓的正是那個。所以這裡讀原始檔，比對的是**檔案裡真的有什麼**。
 *
 * （`.properties` 是 ISO-8859-1 + `\\uXXXX` escape，`Properties.load(InputStream)` 會處理。）
 *
 * ⚠️ [EventCategory] 的 bundle 由 `EventTypeBundleParityTest` 守著，本測試**刻意不重複** ——
 * 同一件事在兩處維護，遲早只有一處被更新。
 */
class SituationBundleParityTest {

  /** 已經存在的語系。新增語系時加在這裡，測試會立刻告訴你缺哪些 key。 */
  private val suffixes = listOf("", "_en", "_ja", "_ko", "_zh_CN")

  private fun load(bundle: String, suffix: String): Properties {
    val path = "/destiny/core/$bundle$suffix.properties"
    val stream = javaClass.getResourceAsStream(path) ?: error("找不到 bundle：$path")
    return Properties().also { props -> stream.use { props.load(it) } }
  }

  private fun assertParity(bundle: String, expectedKeys: Set<String>) {
    val base = load(bundle, "").stringPropertyNames()
    assertEquals(expectedKeys, base, "$bundle.properties 的 key 與 enum 值不符")

    suffixes.forEach { suffix ->
      val props = load(bundle, suffix)
      val keys = props.stringPropertyNames()
      assertEquals(emptySet(), base - keys, "$bundle$suffix 缺少的 key")
      assertEquals(emptySet(), keys - base, "$bundle$suffix 多出的 key")

      val blank = keys.filter { props.getProperty(it).isBlank() }
      assertTrue(blank.isEmpty(), "$bundle$suffix 有空值的 key: $blank")
    }
  }

  @Test
  fun `Situation 五語系與 enum 同步`() {
    assertParity("Situation", Situation.entries.map { "${it.name}.title" }.toSet())
  }

  /**
   * 上面那條比對的是檔案內容，這條確認**接縫真的把 ko 接上了** ——
   * 檔案齊全但 `Lang.KO` 解析錯 locale 的話，使用者看到的仍是繁中。
   *
   * 斷言「與 `_ko` 檔案裡的值相同」而非寫死韓文字串：文案本來就會被潤，
   * 釘住譯文只會讓每次改字都要改測試，而這條真正要防的是**靜默 fallback**。
   */
  @Test
  fun `ko 不再 fallback 成繁中`() {
    Situation.entries.forEach { situation ->
      assertEquals(
        load("Situation", "_ko").getProperty("${situation.name}.title"),
        situation.getTitle(Lang.KO),
        "$situation 的韓文標題沒有走到 _ko bundle"
      )
    }
  }

  /**
   * 健全性檢查：確認上面那些斷言真的掃到了東西。
   *
   * **不寫成「至少 N 個」的門檻** —— 以數量為基準的斷言會隨著資料變動自己走掉。
   * 上一條比對的是 enum 自己的大小，新增處境時它跟著長，永遠不會過期；
   * 這一條只釘 bundle 名稱是怎麼從類別 FQN 推導出來的。
   */
  @Test
  fun `bundle 名稱推導正確`() {
    assertEquals("destiny.core.Situation", Situation::class.java.name)
    assertTrue(I18nBundles.string("destiny.core.Situation", Lang.KO, "OTHERS.title") != null)
  }
}
