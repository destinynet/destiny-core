/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles

import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.IDigestFormat
import java.util.*

interface IOracleDigest<M : IClause> : IDigestFormat<M, String> {

  override val domain: Domain
    get() = Domain.CHANCE

  override fun promptsForExpectingStructure(locale: Locale): String? {
    return null
  }
}
