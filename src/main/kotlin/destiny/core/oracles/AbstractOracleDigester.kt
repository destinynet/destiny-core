package destiny.core.oracles

import destiny.tools.ai.model.AbstractDigestFormat
import destiny.tools.ai.model.FormatSpec

abstract class AbstractOracleDigester<M : IClause> : AbstractDigestFormat<M, String>(FormatSpec.of<String>("oracle" , "oracle content")) {
}
