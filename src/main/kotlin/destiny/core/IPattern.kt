package destiny.core

import destiny.tools.JSerializable
import java.util.*

interface IPattern : JSerializable {

  fun getName(locale: Locale = Locale.TAIWAN): String {
    return javaClass.simpleName
  }

  fun getNotes(locale: Locale): String? {
    return null
  }

}
