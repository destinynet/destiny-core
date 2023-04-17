/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles

import destiny.core.IDigest

interface IOracleDigest<M : IClause> : IDigest<M, String>
