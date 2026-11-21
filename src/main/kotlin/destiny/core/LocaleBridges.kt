/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.core

import destiny.tools.toLang
import java.util.Locale

/*
 * 本 package 的 top-level extension 的 `Locale` 橋接，留給 JVM 端既有的呼叫端。
 *
 * 與 `Lang` 版同名、同 package，所以呼叫端原本的 `import destiny.core.xxx` 會同時帶進兩個多載，不必改 import。
 * 新程式請直接用 `Lang` 版。這是本 package 唯一相依 `Locale` 的檔案（KMP quarantine 上刻意保留），轉 KMP 時移到 jvmMain。
 */

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun Point.getAbbreviation(locale: Locale): String = getAbbreviation(locale.toLang())

fun News.NorthSouth.toCoordinateString(locale: Locale): String = toCoordinateString(locale.toLang())

fun News.EastWest.toCoordinateString(locale: Locale): String = toCoordinateString(locale.toLang())

fun News.getTitle(locale: Locale): String = getTitle(locale.toLang())
