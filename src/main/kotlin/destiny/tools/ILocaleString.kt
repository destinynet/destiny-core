/**
 * @author smallufo
 * Created on 2008/1/19 at 下午 10:36:12
 */
package destiny.tools

/**
 * 可依語系取得標題的東西。
 *
 * ## Locale 版在哪裡
 *
 * [getTitle] 吃 [Lang]（平台中立），是**實作端唯一要實作的方法**。
 * 吃 `java.util.Locale` 的版本是 JVM 橋接：`ILocaleString.getTitle(locale)` 擴充函式（`LangJvm.kt`，
 * 呼叫端要 `import destiny.tools.getTitle`）。
 *
 * 2026-11-22 以前橋接是**介面成員**（呼叫端不必 import），為了讓本介面不相依 `Locale`、能進 KMP 的 commonMain，
 * 改成擴充函式，下游呼叫端一次補齊 import。新程式請直接傳 [Lang]。
 *
 * ⚠️ 同時是 enum 又實作本介面的型別，`x.getTitle(locale)` 會與 `Enum<T>.getTitle(locale)`（`Extensions.kt`）
 * 兩個擴充函式撞名而不明確 —— 改寫成 `x.getTitle(locale.toLang())`。
 *
 * ## 不要再寫 `toString(locale)`／`asLocaleString()`
 *
 * 2008 年的 Java 版本方法名就叫 `toString(Locale)`，後來改成 [getTitle]，但 `Point`、`Dignity` 等
 * 留著 `fun X.toString(locale)` extension 與 `asLocaleString()` 轉接（2026-11-04 全部收掉）。
 * 有名稱的型別請**直接實作本介面**：member 永遠勝過 extension，不會像 `Enum.getTitle` 那樣
 * 在 bundle 格式不同時靜默退回列舉名。
 *
 * 無參數的 `toString()` 若回傳顯示名稱，它只是 debug／預設語系的方便；識別字（JSON、map key、比對、
 * 去重、URL、SQL）一律用 `nameKey`／`name`。
 */
interface ILocaleString : JSerializable {

  fun getTitle(lang: Lang = Lang.DEFAULT): String
}
