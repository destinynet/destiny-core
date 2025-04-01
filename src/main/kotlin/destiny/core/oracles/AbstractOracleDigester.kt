package destiny.core.oracles

import destiny.tools.ai.model.AbstractDigestFormat
import destiny.tools.ai.model.Domain
import kotlinx.serialization.builtins.serializer
import java.util.*

abstract class AbstractOracleDigester<M : IClause> : AbstractDigestFormat<M, String>(String.serializer()) {
  final override val domain: Domain = Domain.CHANCE

  override fun digestWithoutFormat(model: M, locale: Locale): String? {
    return null
  }
}
