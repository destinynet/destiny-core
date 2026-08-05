/**
 * @author smallufo
 * Created on 2008/1/19 at 下午 10:36:12
 */
package destiny.tools

import java.util.*

/**
 * 可依語系取得標題的東西。
 *
 * ## 為什麼有兩個 getTitle
 *
 * [getTitle] 吃 [Lang]（平台中立），是**實作端唯一要實作的方法**。
 * 另一個吃 `java.util.Locale` 的多載是**橋接**，有預設實作，實作端不該覆寫它。
 *
 * 橋接刻意做成**介面成員**而非 extension function：extension 需要呼叫端逐檔 import，
 * 實測會波及下游 124 個檔案（wicket 43、core-impl 42、charts 18…）；
 * 做成成員則呼叫端一行都不必改。
 *
 * 代價是本檔仍相依 `java.util.Locale` —— 但那正是刻意的收斂：
 * **整個 destiny-core 的 Locale 相依集中在這一個方法**（同 [I18nBundles] 之於 ResourceBundle）。
 * 轉 KMP 時把它移到 jvmMain 的 extension，屆時再由編譯器帶著做一次 import 掃描。
 */
interface ILocaleString : JSerializable {

  fun getTitle(lang: Lang = Lang.DEFAULT): String

  /** 橋接：仍以 `Locale` 溝通的呼叫端走這裡。**實作端不要覆寫。** */
  fun getTitle(locale: Locale): String = getTitle(locale.toLang())
}
