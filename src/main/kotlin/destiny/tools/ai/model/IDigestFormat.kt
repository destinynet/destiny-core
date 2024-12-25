/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.tools.ai.model

import destiny.core.IDigest
import java.util.*


interface SerializableData

@kotlinx.serialization.Serializable
sealed interface IDigestResponse : SerializableData


/**
 * expecting LLM to reply in D data structure
 */
interface IDigestFormat<M, T, D : IDigestResponse> : IDigest<M, T> {
  fun promptsForExpectingStructure(locale: Locale = Locale.getDefault()): D
}
