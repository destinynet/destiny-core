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
  private val prettyJson = Json {
    prettyPrint = true
    encodeDefaults = true
    // do not encode null fields
    explicitNulls = false
  }


  final override fun digest(model: M, locale: Locale): String {
    return buildString {
      append(digestWithoutFormat(model, locale))
      appendLine()
      appendLine(prettyJson.encodeToString(serializer, promptsForExpectingStructure(locale)))
      appendLine(finalInstruction(locale))
    }
  }

  abstract fun digestWithoutFormat(model: M, locale: Locale): String?

  fun finalInstruction(locale: Locale): String {
    return buildString {
      append("[FINAL_INSTRUCTION]\n")
      append("Please ensure your entire response is in ${locale.getDisplayLanguage(Locale.ENGLISH)} ( locale = $locale)  (except for the JSON keys), including all terms and interpretations.")
    }
  }
}
