/**
 * Created by smallufo on 2023-04-03.
 */
package destiny.core.tarot

import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.IDigestFormat
import java.util.*


@Deprecated("AbstractTarotDigester")
interface ISpreadDigest<T : ISpread> : IDigestFormat<T , String> {

  override val domain: Domain
    get() = Domain.TAROT

  override fun promptsForExpectingStructure(locale: Locale): String? {
    return null
  }
}
