/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.core.tarot

import destiny.tools.ai.model.AbstractDigestFormat
import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.FormatSpec


abstract class AbstractTarotDigester<T : ISpread> : AbstractDigestFormat<T, String>(FormatSpec.of<String>("tarot" , "tarot content")) {

  final override val domain: Domain = Domain.TAROT
}
