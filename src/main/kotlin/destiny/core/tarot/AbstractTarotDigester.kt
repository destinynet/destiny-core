/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.core.tarot

import destiny.tools.ai.model.AbstractDigestFormat
import destiny.tools.ai.model.Domain
import kotlinx.serialization.builtins.serializer
import java.util.*


abstract class AbstractTarotDigester<T : ISpread> : AbstractDigestFormat<T, String>(String.serializer()) {

  override val domain: Domain = Domain.TAROT

  override fun promptsForExpectingStructure(locale: Locale): String? {
    return null
  }
}
