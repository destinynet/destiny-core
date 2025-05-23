package destiny.core.oracles

import destiny.tools.ai.model.AbstractDigestFormat
import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.FormatSpec

abstract class AbstractOracleDigester<M : IClause> : AbstractDigestFormat<M, String>(FormatSpec.of<String>("oracle" , "oracle content")) {
  final override val domain: Domain = Domain.CHANCE
}
