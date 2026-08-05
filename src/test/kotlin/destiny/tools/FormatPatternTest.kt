/**
 * Created by smallufo on 2026-08-05.
 */
package destiny.tools

import java.io.File
import java.text.MessageFormat
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * [formatPattern] 是 `java.text.MessageFormat` 的平台中立替代品。
 *
 * 取代的正當性由「所有含佔位符的 pattern 與 MessageFormat 逐字相同」證明 ——
 * 那是**窮舉**（掃過 src/main/resources 全部 258 個 .properties），不是抽樣。
 */
class FormatPatternTest {

  @Test
  fun `索引替換`() {
    assertEquals("火星 位於第 3 宮", formatPattern("{0} 位於第 {1} 宮", listOf("火星", 3)))
  }

  /** MessageFormat 的 `''` 是「一個單引號」的逃脫 */
  @Test
  fun `雙單引號還原為單引號`() {
    assertEquals("the kite's front", formatPattern("the kite''s front", emptyList()))
  }

  @Test
  fun `參數不足時原樣保留佔位符`() {
    assertEquals("{0} 與 {1}", formatPattern("{0} 與 {1}", emptyList()))
  }

  @Test
  fun `沒有佔位符就原樣回傳`() {
    assertEquals("純文字", formatPattern("純文字", listOf("A")))
  }

  /**
   * 窮舉比對：掃過 `src/main/resources` 全部 258 個 .properties，
   * 對**每一個含 `{` 的 value**（＝真正會被 format 的 pattern）與 [MessageFormat] 比對。
   *
   * 若日後有人寫進 `{0,number}` / `{0,choice}` 之類 [formatPattern] 不支援的語法，
   * 這個測試會失敗 —— 那是刻意的護欄。
   */
  @Test
  fun `所有含佔位符的 pattern 與 MessageFormat 逐字相同`() {
    val args: Array<Any> = Array(10) { "A$it" }
    var checked = 0

    forEachPropertyValue { fileName, _, value ->
      if ('{' !in value) return@forEachPropertyValue
      assertEquals(
        MessageFormat.format(value, *args),
        formatPattern(value, args.toList()),
        "pattern in $fileName : $value"
      )
      checked++
    }

    println("formatPattern 與 MessageFormat 比對了 $checked 個 pattern")
    // 308 個含佔位符的 value（其中共 1,093 個 {n}）。數字變動請確認是刻意新增的 pattern。
    assertEquals(308, checked, "含佔位符的 value 數變了")
  }

  /**
   * **已知且刻意的差異**：`MessageFormat` 把**落單**的單引號當成 quoted region 的開始並吞掉它
   * （`17'` → `17`）；[formatPattern] 則原樣保留。
   *
   * 這個差異在本 repo 沒有影響，因為落單單引號只出現在**標題**類 value
   * （`Dragon's Tail`、`Sun's rays` 這種英文所有格），而標題走 `getString` 直出、
   * **從不經過 format**。真正會被 format 的 1,377 個 key 裡落單單引號為 0，
   * 8 處引號全是正規的 `''` 逃脫。
   *
   * 本測試釘住這個前提：**任何含 `{` 的 value 都不得有落單單引號**。
   * 若日後有人寫出 `{0} 的 Dragon's Tail`，這裡會失敗，屆時要嘛把它改成 `''`，
   * 要嘛讓 [formatPattern] 完整複製 MessageFormat 的引號規則。
   */
  @Test
  fun `會被 format 的 value 不得含落單單引號`() {
    val offenders = mutableListOf<String>()

    forEachPropertyValue { fileName, key, value ->
      if ('{' !in value) return@forEachPropertyValue
      if ('\'' in value.replace("''", "")) offenders += "$fileName : $key = $value"
    }

    assertEquals(emptyList(), offenders, "含 { 的 value 出現落單單引號，見 KDoc")
  }

  /** `Properties.load` 以 ISO-8859-1 讀入並還原 `\\uXXXX`，與 `ResourceBundle` 的讀法一致 */
  private fun forEachPropertyValue(block: (fileName: String, key: String, value: String) -> Unit) {
    File("src/main/resources").walkTopDown()
      .filter { it.isFile && it.name.endsWith(".properties") }
      .forEach { f ->
        Properties().apply { f.inputStream().use { load(it) } }
          .forEach { (k, v) -> block(f.name, k as String, v as String) }
      }
  }
}
