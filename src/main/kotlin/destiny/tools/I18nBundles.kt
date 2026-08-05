/**
 * Created by smallufo on 2026-08-05.
 */
package destiny.tools

import java.util.*
import kotlin.reflect.KClass

/**
 * **destiny-core 內唯一允許碰 [ResourceBundle] 的地方。**
 *
 * 這是 i18n 的 single point of failure，而且是刻意的 —— 原本散落在 23 個檔案的
 * 33 個 `ResourceBundle.getBundle(x, locale).getString(k)` 全部收斂到這裡。
 * 手法與 [JSerializable] 相同：**把 N 個檔案的問題變成 1 個檔案的問題**。
 *
 * ## 為什麼簽章吃 [Lang] 而非 `java.util.Locale`
 *
 * 這一層已經是 KMP-ready 的形狀，`Locale` 只活在本檔的 body 裡。
 * 未來 destiny-core 轉 Gradle/KMP 時，body 換成「build 時由 .properties 產生的 Kotlin 查表」，
 * **33 個呼叫端一行都不用改**。屆時 .properties 仍是翻譯來源
 * （[Lang.resourceSuffix] 已對應 `*_zh_TW.properties` 的命名慣例）。
 *
 * 詳見 `destiny-core-impl/docs/plans/2026-08-05-i18n-seam.md`。
 *
 * ## 與 ResourceBundle 的刻意差異
 *
 * 查不到時回傳 `null` 而非拋 `MissingResourceException`。原本有 4 處在 catch 這個例外，
 * 改用 `?:` 更直接，也讓日後的 commonMain 實作不必模擬 JVM 的例外型別。
 */
object I18nBundles {

  /**
   * @param bundle bundle 的完整名稱，通常來自 [bundleName]
   * @return 查不到（bundle 不存在或 key 不存在）時回傳 `null`
   *
   * **注意 `ResourceBundle` 的 fallback 鏈包含行程預設語系**：查不到指定語系時，
   * 它會先試 `Locale.getDefault()` 的 bundle，最後才是 base bundle。
   * 本專案預設 zh_TW，所以查 `ko` 會得到 `_zh` 的內容而非 base。
   * 這個隱式行為由 `I18nBundlesTest` 釘住 —— 日後換成 codegen 查表時，
   * 它不會自動存在，必須明確決定要複製還是移除。
   */
  fun string(bundle: String, lang: Lang, key: String): String? {
    return try {
      ResourceBundle.getBundle(bundle, lang.toLocale()).getString(key)
    } catch (e: MissingResourceException) {
      null
    }
  }
}

/**
 * `java.text.MessageFormat` 的平台中立替代品。
 *
 * **只支援兩條規則**，因為本 repo 只用到這兩條（2026-08-05 實測 258 個 .properties、
 * 1,093 個佔位符，**100% 是單純 `{n}`**，零 `{0,number}` / `{0,choice}`；8 行用到 `''`）：
 *
 *  1. `{n}` → `args[n]`（索引不存在時原樣保留佔位符）
 *  2. `''`  → `'`
 *
 * `FormatPatternTest` 掃過全部 bundle 與 `MessageFormat` 逐字比對，證明等價。
 * 若日後有人在 .properties 寫進不支援的語法，該測試會失敗 —— 那是刻意的護欄。
 */
fun formatPattern(pattern: String, args: List<Any?>): String {
  if ('{' !in pattern && '\'' !in pattern) return pattern

  val sb = StringBuilder(pattern.length)
  var i = 0
  while (i < pattern.length) {
    val c = pattern[i]
    when {
      c == '\'' && i + 1 < pattern.length && pattern[i + 1] == '\'' -> {
        sb.append('\'')
        i += 2
      }

      c == '{'                                                      -> {
        val close = pattern.indexOf('}', i + 1)
        val idx = if (close > i) pattern.substring(i + 1, close).toIntOrNull() else null
        when {
          idx == null           -> { sb.append(c); i++ }
          idx in args.indices   -> { sb.append(args[idx].toString()); i = close + 1 }
          // 參數不足：原樣保留，與 MessageFormat 行為一致
          else                  -> { sb.append(pattern, i, close + 1); i = close + 1 }
        }
      }

      else                                                          -> { sb.append(c); i++ }
    }
  }
  return sb.toString()
}

/**
 * bundle 名稱的推導接縫。
 *
 * 目前是 `java.name`。未來進 commonMain 時，**這一行是唯一要面對
 * 「`KClass.qualifiedName` 在各平台支援度」的地方** —— 屆時可換成 `qualifiedName`，
 * 或改由 codegen 產生的對應表。呼叫端同樣不動。
 */
fun KClass<*>.bundleName(): String = this.java.name
