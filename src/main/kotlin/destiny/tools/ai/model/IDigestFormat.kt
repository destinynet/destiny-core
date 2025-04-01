/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.tools.ai.model

import destiny.core.IDigest
import java.util.*

/**
 * expecting LLM to reply in D data structure
 */
interface IDigestFormat<M, D> : IDigest<M, String> {

  val domain: Domain

  fun promptsForExpectingStructure(model: M, locale: Locale = Locale.getDefault()): D? {
    return null
  }
}

