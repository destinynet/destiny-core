/**
 * Created by smallufo on 2025-01-14.
 */
package destiny.tools

import java.util.*


interface ITranslate {

  fun translate(raw: String, fromLocale: Locale, toLocale: Locale): String
}
