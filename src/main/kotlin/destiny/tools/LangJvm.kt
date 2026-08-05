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

/**
 * 本專案的預設語系 —— [Lang.DEFAULT] 的 `Locale` 形式（`zh_TW`）。
 *
 * **用來取代散落各處的 `Locale.getDefault()`。** 後者是行程層級的隱式全域狀態，
 * 同一段程式在不同機器／不同 JVM 參數下會給出不同結果 —— 本 repo 已被同類問題咬過
 * （`String.format` 未帶 locale，小數點隨系統語系變逗號，`688b5a59` 修）。
 * 而 commonMain 根本沒有 ambient default 這個概念。
 *
 * 伺服器上 `Locale.getDefault()` 本來就是 zh_TW（見 CLAUDE.md），
 * 所以這個替換在 prod 是**零行為變更**，只是把「碰巧正確」變成「明確正確」。
 *
 * 階段 2（`getTitle(Locale)` → `getTitle(Lang)`）完成後，這些呼叫點會直接換成 [Lang.DEFAULT]。
 * 由 `NoAmbientLocaleTest` 擋住回歸。
 */
val defaultLocale: Locale = Lang.DEFAULT.toLocale()
