/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles.taigi

import destiny.core.oracles.IClause
import destiny.tools.Lang

@JvmInline
value class Taigi(val content : String) : IClause {
  override fun getTitle(lang: Lang): String {
    return "太極神數"
  }
}
