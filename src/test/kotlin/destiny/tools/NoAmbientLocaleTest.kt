/**
 * Created by smallufo on 2026-08-06.
 */
package destiny.tools

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * **destiny-core 不得依賴 `Locale.getDefault()`。**
 *
 * `Locale.getDefault()` 是**行程層級的隱式全域狀態** —— 同一段程式在不同機器、
 * 不同 JVM 參數下會給出不同結果。這在本 repo 已經咬過一次：
 * `String.format` 未帶 locale，導致小數點隨系統語系變成逗號（`688b5a59` 修）。
 *
 * 而 commonMain 根本沒有 ambient default 這個概念，所以這些呼叫點遲早都要面對。
 * 與其等階段 2 一次面對 47 個決定，不如現在就一律換成明確常數 [defaultLocale]
 * （＝ [Lang.DEFAULT] 的 `Locale` 形式）。
 *
 * 伺服器上 `Locale.getDefault()` 本來就是 zh_TW（見 CLAUDE.md），
 * 所以這個替換在 prod 是**零行為變更**，只是把「碰巧正確」變成「明確正確」。
 *
 * 例外：本檔與 [defaultLocale] 的定義處。
 */
class NoAmbientLocaleTest {

  @Test
  fun `src main 不得出現 Locale getDefault`() {
    val offenders = mutableListOf<String>()

    File("src/main/kotlin").walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .forEach { f ->
        f.readLines().forEachIndexed { idx, raw ->
          val line = raw.trim()
          // 註解裡提到它是說明，不是相依
          if (line.startsWith("//") || line.startsWith("*") || line.startsWith("/*")) return@forEachIndexed
          if (!line.contains("Locale.getDefault()")) return@forEachIndexed
          // defaultLocale 的定義處本身必須提到它（用來說明取代了什麼）—— 但那是 KDoc，已被上面濾掉
          offenders += "${f.path.removePrefix("src/main/kotlin/")}:${idx + 1}  $line"
        }
      }

    assertEquals(
      emptyList(), offenders,
      "請改用 destiny.tools.defaultLocale（階段 2 後會變成 Lang.DEFAULT）"
    )
  }
}
