/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.tools

import java.util.Locale

/**
 * 依語系輸出字串的 decorator（例：`ZodiacDegreeDecorator`、`DateDecorator`）。
 *
 * 實作端只實作 [Lang] 版。[Locale] 版是給 JVM 端既有呼叫端的**橋接成員**，勿覆寫 ——
 * 做成成員而不是 extension，呼叫端才不必補 import（同 [ILocaleString] 的做法）。
 * 整個 destiny-core 的 decorator 只有這個檔案相依 [Locale]；轉 KMP 時橋接移到 jvmMain。
 */
interface LocalizedDecorator<in T> {

  fun getOutputString(value: T, lang: Lang): String

  /** `Locale` 橋接，勿覆寫 */
  fun getOutputString(value: T, locale: Locale): String = getOutputString(value, locale.toLang())
}

/** 舊的 [Locale] 鍵版本，留給 JVM 端既有的呼叫端；新程式用 `Map<Lang, Decorator<T>>.getOutputString` */
fun <T> Map<Locale, Decorator<T>>.getOutputString(value: T, locale: Locale): String {
  return this.getValue(LocaleTools.getBestMatchingLocaleOrFirst(locale, this.keys)).getOutputString(value)
}
