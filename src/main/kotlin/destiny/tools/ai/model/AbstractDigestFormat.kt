package destiny.tools.ai.model

import destiny.tools.ai.JsonSchemaSpec
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


  override fun digest(model: M, locale: Locale): Pair<String, JsonSchemaSpec?> {
    val (structurePromptingJson: String?, schema: JsonSchemaSpec?) = promptsForExpectingStructure(model, locale)?.let { (structurePrompting, schema) ->
      prettyJson.encodeToString(serializer, structurePrompting) to schema
    } ?: (null to null)

    val string = buildString {
      append(digestWithoutFormat(model, locale))
      appendLine()

      structurePromptingJson?.also {
        appendLine(it)
      }
      appendLine(finalInstruction(model, locale))
    }
    return string to schema
  }

  abstract fun digestWithoutFormat(model: M, locale: Locale): String?

  fun finalInstruction(model : M , locale: Locale): String {
    return buildString {
      append("[FINAL_INSTRUCTION]\n")
      append("Please ensure your entire response is in ${locale.getDisplayLanguage(Locale.ENGLISH)} ( locale = $locale )")
      if (promptsForExpectingStructure(model, locale) != null) {
        append("(except for the JSON keys)")
      }
      append(", including all terms and interpretations.")
    }
  }
}
