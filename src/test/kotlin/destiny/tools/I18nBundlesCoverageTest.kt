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
 *
 * 這條檢查只對「存在但查不到」有效，對「整個 key 消失」是盲的 —— 沒有 key 就沒有東西可迭代。
 * 那個盲點交給 [`語系檔的 key 集合必須與 base 一致`]：它比對集合而非計數，
 * 所以能指出**哪個檔案少了哪幾個 key**。
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
  }

  /**
   * 每個語系檔的 key 集合必須與同家族的 base bundle **完全相同**。
   *
   * 這條取代了原本的 `assertEquals(3782, checked)` 總數哨兵。那個數字捕捉的是真實的盲點
   * （key 消失時上面那條檢查看不到），但用計數去守集合有三個毛病：
   * 失敗訊息只說「總數變了」、不說哪裡變；每次正常新增翻譯都會紅，而修法是「把數字改大」，
   * 久了就變成不看內容照改；而且 `+13 −1` 與 `+12` 在計數上無法區分。
   * 比對集合則直接指出檔名與缺漏的 key。
   *
   * 只檢查**已存在**的語系檔 —— 規則是「翻了就要翻完」，不是「每個 enum 都要翻成每種語言」
   * （本模組 100 個家族中只有 15 個有 `_ja`，另有 2 個連一個語系檔都沒有）。
   * 因此「整個語系檔被刪掉」仍在盲區，但那在 diff 裡是顯眼的，且 enum 那側另有
   * `EnumTest.assertBundleParity` 會要求至少存在一個語系檔。
   */
  @Test
  fun `語系檔的 key 集合必須與 base 一致`() {
    // "<父目錄>/<家族名>" → (檔名後綴 → 檔案)；後綴 "" 即 base
    val families = mutableMapOf<String, MutableMap<String, File>>()

    File("src/main/resources").walkTopDown()
      .filter { it.isFile && it.name.endsWith(".properties") }
      .forEach { f ->
        val name = f.name.removeSuffix(".properties")
        // suffixToLang 已由長到短排序，_zh_TW 才不會被 _zh 先吃掉
        val suffix = suffixToLang.map { it.first }.firstOrNull { name.endsWith(it) } ?: ""
        families.getOrPut("${f.parent}/${name.removeSuffix(suffix)}") { mutableMapOf() }[suffix] = f
      }

    fun keysOf(f: File): Set<String> = Properties()
      .apply { f.inputStream().use { load(it) } }
      .stringPropertyNames()

    val problems = mutableListOf<String>()
    families.forEach { (family, bySuffix) ->
      val baseFile = bySuffix[""]
      if (baseFile == null) {
        problems += "$family 沒有 base bundle（只有語系檔）"
        return@forEach
      }
      val baseKeys = keysOf(baseFile)
      bySuffix.filterKeys { it.isNotEmpty() }.forEach { (_, f) ->
        val k = keysOf(f)
        (baseKeys - k).takeIf { it.isNotEmpty() }?.let { problems += "${f.name} 缺少翻譯：${it.sorted()}" }
        (k - baseKeys).takeIf { it.isNotEmpty() }?.let { problems += "${f.name} 有多餘 key：${it.sorted()}" }
      }
    }

    assertEquals(emptyList(), problems)
    println("bundle parity：${families.size} 個家族、${families.values.sumOf { it.size }} 個檔案")
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
