/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

import java.util.Locale

/**
 * [Lang] 與 [Locale] 的橋接。
 *
 * 刻意獨立成一個檔案：[Lang] 本身必須維持零 JVM 相依（才能進 `commonMain`），
 * 而本檔天生依賴 `java.util.Locale`，故列入 `kmp-quarantine.txt`。
 *
 * 轉為 KMP 後本檔歸 `jvmMain`，讓 destiny-springboot / destiny-wicket 等
 * 仍以 [Locale] 溝通的呼叫端不必改動。
 */

/** [Locale] → [Lang]。`Locale.ROOT`（language tag 為 `"und"`）轉為 [Lang.ROOT] */
fun Locale.toLang(): Lang = Lang.of(this.toLanguageTag()) ?: Lang.ROOT

/** [Lang] → [Locale]。[Lang.ROOT] 轉為 `Locale.ROOT` */
fun Lang.toLocale(): Locale = if (isRoot) Locale.ROOT else Locale.forLanguageTag(tag)
