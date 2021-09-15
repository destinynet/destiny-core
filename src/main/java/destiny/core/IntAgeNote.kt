/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

import destiny.core.calendar.GmtJulDay
import java.util.*

/**
 * 將 [IIntAge] 計算出來的結果 Pair[GMT , GMT] 附註年份
 * 例如，西元年份、或是民國年份、或是中國歷史紀元
 */
interface IntAgeNote : Descriptive {

  /** 此時刻的註記 ( 通常只註記「西元XX年」 )  */
  fun getAgeNote(gmtJulDay: GmtJulDay): String?

  /**
   * @param startAndEnd [from GMT, to GMT] 時刻
   */
  fun getAgeNote(startAndEnd: Pair<GmtJulDay, GmtJulDay>): String?
}

enum class IntAgeNoteImpl {
  WestYear,
  Minguo
}

fun IntAgeNoteImpl.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(this@asDescriptive.javaClass.name, locale).getString("${name}.name")
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }
  }
}

fun IntAgeNoteImpl.toString(locale: Locale): String {
  return this.asDescriptive().toString(locale)
}
