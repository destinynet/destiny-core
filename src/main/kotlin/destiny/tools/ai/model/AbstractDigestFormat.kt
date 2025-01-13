package destiny.tools.ai.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.util.*

abstract class AbstractDigestFormat<M, D>(
  val serializer: KSerializer<D>
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
      promptsForExpectingStructure(locale)?.also { structurePrompting ->
        appendLine(prettyJson.encodeToString(serializer, structurePrompting))
      }
      appendLine(finalInstruction(locale))
    }
  }

  abstract fun digestWithoutFormat(model: M, locale: Locale): String?

  fun finalInstruction(locale: Locale): String {
    return buildString {
      append("[FINAL_INSTRUCTION]\n")
      append("Please ensure your entire response is in ${locale.getDisplayLanguage(Locale.ENGLISH)} ( locale = $locale )")
      if (promptsForExpectingStructure(locale) != null) {
        append("(except for the JSON keys)")
      }
      append(", including all terms and interpretations.")
    }
  }
}
