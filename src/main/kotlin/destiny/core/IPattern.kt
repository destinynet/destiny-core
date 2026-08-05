package destiny.core

import destiny.tools.JSerializable
import destiny.tools.Lang
import destiny.tools.toLang
import java.util.*

interface IPattern : JSerializable {

  fun getName(lang: Lang = Lang.DEFAULT): String {
    return javaClass.simpleName
  }

  fun getNotes(lang: Lang): String? {
    return null
  }

  /** 橋接，說明見 [destiny.tools.ILocaleString]。**實作端不要覆寫。** */
  fun getName(locale: Locale): String = getName(locale.toLang())

  /** 橋接，說明見 [destiny.tools.ILocaleString]。**實作端不要覆寫。** */
  fun getNotes(locale: Locale): String? = getNotes(locale.toLang())
}
