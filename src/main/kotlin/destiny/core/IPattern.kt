package destiny.core

import java.io.Serializable
import java.util.*

interface IPattern : Serializable {

  fun getName(locale: Locale = Locale.TAIWAN): String {
    return javaClass.simpleName
  }

  fun getNotes(locale: Locale): String? {
    return null
  }

}
