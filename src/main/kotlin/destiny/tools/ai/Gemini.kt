/**
 * Created by smallufo on 2023-12-31.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


class Gemini {

  @Serializable
  data class Content(val role: String, val parts: List<Part>?) {
    @Serializable
    data class Part(
      val text: String?,
      @SerialName("inline_data") val inlineData: InlineData? = null,
      val functionCall: FunctionCall? = null,
      val functionResponse: FunctionResponse? = null
    ) {
      @Serializable
      data class InlineData(val mimeType: String, val data: String)

      @Serializable
      data class FunctionCall(val name: String, val args: JsonElement)

      @Serializable
      data class FunctionResponse(val name: String, val response: Response) {
        @Serializable
        data class Response(
          val name: String,
          val content: String
        )
      }
    }
  }

  @Serializable
  data class Candidate(
    val content: Content,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null,
    val citationMetadata: CitationMetadata? = null,
    val index: Int? = null
    // val tokenCount: Int? = null // If API provides it
  )

  @Serializable
  data class SafetyRating(
    val category: String,
    val probability: String, // e.g., "NEGLIGIBLE", "LOW", "MEDIUM", "HIGH"
    val blocked: Boolean? = null
  )

  @Serializable
  data class CitationMetadata(
    val citationSources: List<CitationSource>? = null
  ) {
    @Serializable
    data class CitationSource(
      val startIndex: Int? = null,
      val endIndex: Int? = null,
      val uri: String? = null,
      val license: String? = null
    )
  }

  @Serializable
  data class Request(
    val contents: List<Content>,
    val tools: List<Tool>? = null,
    @SerialName("generationConfig")
    val config: GenerationConfig,
    val safetySettings: List<SafetySetting>? = null // Often used
  ) {
    @Serializable
    data class GenerationConfig(
      /**
       * max
       * gemini-pro : 8192 ,
       * gemini-pro-vision : 2048
       */
      val maxOutputTokens: Int? = 2048,
      /**
       * 0 to 2.0
       */
      val temperature: Double = 0.9,
      val topP: Double? = 0.75,
      /**
       * Default for gemini-pro-vision: 32
       * Default for gemini-pro: none
       */
      val topK: Int? = null
    )


  }

  @Serializable
  data class SafetySetting(
    val category: String, // e.g., "HARM_CATEGORY_SEXUALLY_EXPLICIT"
    val threshold: String // e.g., "BLOCK_MEDIUM_AND_ABOVE"
  )

  @Serializable
  data class Tool(@SerialName("function_declarations") val functionDeclarations: List<FunctionDeclaration>)

  @Serializable
  data class FunctionDeclaration(val name: String, val description: String, val parameters: Parameters) {
    @Serializable
    data class Parameters(val type: String, val properties: Map<String, Argument>, val required: List<String>) {
      @Serializable
      data class Argument(val type: String, val description: String)
    }
  }

  sealed class ResponseContainer {

    @Serializable
    data class SuccessResponse(
      val candidates: List<Candidate>,
      val usageMetadata: UsageMetadata,
      val modelVersion: String,
      val responseId: String
      ) : ResponseContainer() {

      @Serializable
      data class UsageMetadata(
        val promptTokenCount: Int,
        val candidatesTokenCount: Int,
        val totalTokenCount: Int,
        val thoughtsTokenCount: Int?
      )

      @Serializable
      data class PromptFeedback(
        val blockReason: String? = null,
        val safetyRatings: List<SafetyRating>? = null
      )
    }

    /**
     * for streamGenerateContent
     */
    @Serializable
    data class StreamCandidateContainer(val candidates: List<Candidate>) : ResponseContainer()

    @Serializable
    data class ErrorContainer(val error: ErrorDetails?) : ResponseContainer() {
      @Serializable
      data class ErrorDetails(val code: Int, val message: String, val status: String)
    }

    companion object {
      /** contents with functionCall */
      fun List<Candidate>.contentsWithFunctionCall(): List<Content> {
        return this.mapNotNull { candidate -> candidate.content.takeIf { c -> c.parts?.any { p -> p.functionCall != null } ?: false } }
      }
    }
  }
}


