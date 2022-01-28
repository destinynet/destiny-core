/**
 * @author smallufo
 * Created on 2008/1/19 at 下午 10:36:12
 */
package destiny.tools

import java.io.Serializable
import java.util.*

interface ILocaleString : Serializable {

  fun toString(locale: Locale = Locale.getDefault()): String
}
