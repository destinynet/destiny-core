/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.tools

import java.util.Locale

/**
 * 以 `printf` 樣式格式化，**不受系統語系影響**（小數點固定是「.」，沒有千分位）。
 *
 * 給人看的數字（`"%.1f"`、`"%.2f°"`）若直接用 `String.format`，會跟著 JVM 預設語系走 ——
 * fr／es 環境下小數點會變成逗號。
 *
 * 這是一個**接縫**：目前以 `String.format(Locale.ROOT, …)` 實作，是 destiny-core 裡唯一直接呼叫它的地方。
 * 日後進 KMP 時改成 `expect`／`actual`（或改寫成自己的定點格式化），呼叫端不必改。
 */
fun String.formatInvariant(vararg args: Any?): String = String.format(Locale.ROOT, this, *args)
