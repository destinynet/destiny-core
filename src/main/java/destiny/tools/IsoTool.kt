/**
 * Created by smallufo on 2016-03-01.
 */
package destiny.tools

import java.io.Serializable
import java.util.*

class IsoTool : Serializable {
  companion object {

    /** 小寫 2-letter 英文字母 , 例如 'zh'  */
    private val ISO_LANGUAGES: Set<String> = Locale.getISOLanguages().toSet()

    /** 大寫 2-letter 英文字母 , 例如 'TW'  */
    private val ISO_COUNTRIES: Set<String> = Locale.getISOCountries().toSet()

    /** 大小寫通吃，即使傳入 'ZH' 也會轉成 'zh' ，並且傳回 true  */
    fun isValidLanguage(s: String): Boolean {
      return ISO_LANGUAGES.contains(s.lowercase(Locale.getDefault()))
    }

    /** 大小寫通吃，即使傳入 'tw' 也會轉成 'TW' ，並且傳回 true  */
    fun isValidCountry(s: String): Boolean {
      return ISO_COUNTRIES.contains(s.uppercase(Locale.getDefault()))
    }
  }
}
