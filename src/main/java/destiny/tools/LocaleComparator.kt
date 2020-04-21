/**
 * Created by smallufo on 2016-02-22.
 */
package destiny.tools

import java.io.Serializable
import java.util.*
import kotlin.Comparator

class LocaleComparator(private val locale: Locale) : Comparator<Locale>, Serializable {

  override fun compare(locale1: Locale, locale2: Locale): Int {
    return when {
      locale1 == locale2 -> 0
      locale1 == locale -> -1
      locale2 == locale -> 1
      locale1.language == locale.language -> -1
      locale2.language == locale.language -> 1
      Locale.ENGLISH.language == locale1.language -> -1
      Locale.ENGLISH.language == locale2.language -> 1
      else -> 1
    }
  }
}
