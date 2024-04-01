/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:20:43
 */
package destiny.tools

import java.util.*


fun <T> Map<Locale, Decorator<T>>.getOutputString(value: T, locale: Locale): String {
  return this.getValue(LocaleTools.getBestMatchingLocaleOrFirst(locale, this.keys)).getOutputString(value)
}

interface Decorator<in T> {

  fun getOutputString(value: T): String
}
