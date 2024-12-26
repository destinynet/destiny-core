/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.tools.ai.model

import destiny.core.IDigest
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.util.*


interface SerializableData

@kotlinx.serialization.Serializable
sealed interface IDigestResponse : SerializableData


/**
 * expecting LLM to reply in D data structure
 */
interface IDigestFormat<M, D : IDigestResponse> : IDigest<M, String> {
  fun promptsForExpectingStructure(locale: Locale = Locale.getDefault()): D
}

abstract class AbstractDigestFormat<M, D : IDigestResponse>(
  private val serializer: KSerializer<D>
) : IDigestFormat<M, D> {
  val prettyJson = Json {
    prettyPrint = true
    encodeDefaults = true
  }


  final override fun digest(model: M, locale: Locale): String {
    return buildString {
      append(digestWithoutFormat(model, locale))
      appendLine()
      appendLine(prettyJson.encodeToString(serializer, promptsForExpectingStructure(locale)))
    }
  }

  abstract fun digestWithoutFormat(model: M, locale: Locale): String?
}
