/**
 * Created by smallufo on 2026-08-05.
 */
package destiny.tools

import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * **這個測試是「把 `getString` 的例外改成 `?:` 兜底」這件事的安全網。**
 *
 * 原本 `ResourceBundle.getString(key)` 查不到會拋 `MissingResourceException`，
 * 多數呼叫點沒有 catch —— 等於大聲失敗。改走 [I18nBundles.string] 之後回傳 null，
 * 呼叫端用 `?: nameKey` 兜底，變成**靜默**退回 key 字串。
 *
 * 靜默只有在「fallback 實際上永遠不會觸發」時才安全。本測試逐一驗證
 * `src/main/resources` 底下**每個 .properties 的每個 key**，在其對應語系都查得到。
 */
class I18nBundlesCoverageTest {

  /** 檔名後綴 → [Lang]。本 repo 實際用到的只有這五種（另有 100 個無後綴的 base） */
  private val suffixToLang = listOf(
    "_zh_TW" to Lang.ZH_TW,
    "_zh_CN" to Lang.ZH_CN,
    "_zh" to Lang.ZH,
    "_en" to Lang.EN,
    "_ja" to Lang.JA,
  )

  @Test
  fun `所有 bundle 的所有 key 都查得到`() {
    var checked = 0
    val missing = mutableListOf<String>()

    File("src/main/resources").walkTopDown()
      .filter { it.isFile && it.name.endsWith(".properties") }
      .forEach { f ->
        val relative = f.path.substringAfter("src/main/resources/").removeSuffix(".properties")
        val (bundle, lang) = split(relative)

        Properties().apply { f.inputStream().use { load(it) } }.keys.forEach { k ->
          val key = k as String
          if (I18nBundles.string(bundle, lang, key) == null) missing += "$bundle [$lang] $key"
          checked++
        }
      }

    assertEquals(emptyList(), missing, "有 key 查不到 —— 呼叫端的 ?: fallback 會被觸發")
    println("I18nBundles 覆蓋檢查：$checked 個 key")
    // key 總數變動請確認是刻意新增/刪除的翻譯
    assertEquals(3782, checked, "key 總數變了")
  }

  /** `destiny/core/astrology/Star_zh_CN` → (`destiny.core.astrology.Star`, [Lang.ZH_CN]) */
  private fun split(relativePathWithoutExtension: String): Pair<String, Lang> {
    val dotted = relativePathWithoutExtension.replace('/', '.')
    suffixToLang.forEach { (suffix, lang) ->
      if (dotted.endsWith(suffix)) return dotted.removeSuffix(suffix) to lang
    }
    return dotted to Lang.ROOT
  }

  /** 檔名解析本身也要對 —— 這是上面那個測試的前提 */
  @Test
  fun `檔名解析`() {
    assertEquals("destiny.core.astrology.Star" to Lang.ZH_CN, split("destiny/core/astrology/Star_zh_CN"))
    assertEquals("destiny.core.astrology.Star" to Lang.ZH, split("destiny/core/astrology/Star_zh"))
    assertEquals("destiny.core.astrology.Star" to Lang.ROOT, split("destiny/core/astrology/Star"))
  }
}
