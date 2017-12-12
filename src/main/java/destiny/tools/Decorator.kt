/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:20:43
 */
package destiny.tools

import java.util.*


fun <T> Map<Locale, Decorator<T>>.getOutputString(value : T, locale : Locale) : String {
  return this[LocaleTools.getBestMatchingLocale(locale , this.keys) ?: this.keys.first() ]!!.getOutputString(value)
}

interface Decorator<in T> {

  fun getOutputString(value: T): String
}
