package destiny.core

import java.io.Serializable
import java.util.*

/** 性別  */
enum class Gender(val isMale: Boolean) : Serializable {

  男(true), 女(false);

  override fun toString(): String {
    return GenderDecorator.getOutputString(this, Locale.getDefault())
  }

  fun toString(locale: Locale): String {
    return GenderDecorator.getOutputString(this, locale)
  }

}
