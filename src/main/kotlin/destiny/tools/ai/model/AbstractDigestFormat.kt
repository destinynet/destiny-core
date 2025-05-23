package destiny.tools.ai.model

import destiny.core.IBirthDataNamePlace
import destiny.tools.ai.JsonSchemaSpec
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.util.*

/**
 * [M] : 輸入的 Model object , 例如 [IBirthDataNamePlace]
 * [D] : 預期輸出的 Model , 例如 [BirthDataReply]
 */
abstract class AbstractDigestFormat<M, D : Any>(
  val formatSpec: FormatSpec<D>
) : IDigestFormat<M, D> {

  val serializer: KSerializer<D> = formatSpec.serializer

  override val schema: JsonSchemaSpec? = formatSpec.jsonSchema

  private val prettyJson = Json {
    prettyPrint = true
    encodeDefaults = true
    // do not encode null fields
    explicitNulls = false
  }


  override fun digest(model: M, locale: Locale): Pair<String, FormatSpec<D>?> {
    val structurePromptingJson: String? = promptsForExpectingStructure(model, locale)?.let { structurePrompting ->
      prettyJson.encodeToString(formatSpec.serializer, structurePrompting)
    }

    val string = buildString {
      append(digestWithoutFormat(model, locale))
      appendLine()

      structurePromptingJson?.also {
        appendLine(it)
      }
      appendLine(finalInstruction(model, locale))
    }
    return string to formatSpec
  }

  abstract fun digestWithoutFormat(model: M, locale: Locale): String?

  fun finalInstruction(model: M, locale: Locale): String {
    return buildString {
      appendLine("[FINAL_INSTRUCTION]")
      append("Please ensure your entire response is in ${locale.getDisplayLanguage(Locale.ENGLISH)} (locale = $locale)")
      if (promptsForExpectingStructure(model, locale) != null) {
        append("(except for the JSON keys)")
      }
      appendLine(", including all terms and interpretations.")

      if (schema != null && formatSpec.kClass != String::class) {
        appendLine("""
          Your response should be in JSON format.
          Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
          Remove the ```json markdown surrounding the output including the trailing "```".
        """.trimIndent())
      } else {
        appendLine("""
          Your response should be in TEXT format. DO NOT include any JSON or XML tag.
        """.trimIndent())
      }
    }
  }
}
