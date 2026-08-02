/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

/**
 * [java.io.Serializable] 的別名，作為未來 KMP 化的接縫 (seam)。
 *
 * 目前 destiny-core 仍是純 JVM module，此 typealias 於編譯期展開為 [java.io.Serializable]，
 * JVM 可見的結構（介面、欄位、方法簽章）完全不變 ——
 * Wicket 的 page store、Spring Session JDBC、以及 JCache 的 store-by-value
 * (見 [JCacheTools.createDefaultCache]) 之行為皆不受影響。
 *
 * 導入時的實測驗證（2026-08-03，251 檔替換前後對比）：
 *  - 1775 個 Serializable class 的 `serialVersionUID` 全數不變
 *  - 3478 個 class 檔中 632 個 bytecode 有差異，但 `javap -p` 結構逐字相同；
 *    差異僅在 Kotlin `@Metadata` 額外記錄了 typealias 的 abbreviation
 *    (`Ldestiny/tools/JSerializable;`)，JVM descriptor 仍是 `Ljava/io/Serializable;`。
 *    該欄位只供 Kotlin reflection / IDE 使用，與 Java 序列化無關。
 *
 * 日後若 destiny-core 轉為 KMP，本檔案改寫為：
 * ```
 * // commonMain :  expect interface JSerializable
 * // jvmMain    :  actual typealias JSerializable = java.io.Serializable
 * // iosMain    :  actual interface JSerializable
 * ```
 * 即可讓 200+ 個 data class 一併進入 commonMain，而 JVM 側行為分毫不變。
 */
typealias JSerializable = java.io.Serializable
