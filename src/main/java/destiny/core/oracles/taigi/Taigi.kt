/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles.taigi

import destiny.core.oracles.IClause
import java.util.*

data class Taigi(val content : String) : IClause {
  override fun getTitle(locale: Locale): String {
    return "太極神數"
  }
}
