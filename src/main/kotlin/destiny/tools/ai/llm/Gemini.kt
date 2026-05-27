/**
 * Created by smallufo on 2023-12-31.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.IFunctionDeclaration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


class Gemini {

  @Serializable
  data class Content(val role: String, val parts: List<Part>?) {
    @Serializable
    data class Part(
      val text: String? = null,
      @SerialName("inline_data") val inlineData: InlineData? = null,
      val functionCall: FunctionCall? = null,
      val functionResponse: FunctionResponse? = null,
      /**
       * Gemini 3.x function-calling 必須的 opaque token：model 回傳時帶上，下一輪 request
       * 要把帶 functionCall 的同一個 Part 原樣送回去（包含此欄位）。2.x 為 null，沒影響。
       * https://ai.google.dev/gemini-api/docs/thought-signatures
       */
      val thoughtSignature: String? = null,
      /**
       * thinking model 的 thought summary part flag：true 表示這個 part 是模型的思考摘要
       * （不是最終回答）。需要在 GenerationConfig.thinkingConfig.includeThoughts=true 才會回傳。
       * https://ai.google.dev/gemini-api/docs/thinking
       */
      val thought: Boolean? = null
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
    val safetySettings: List<SafetySetting>? = null, // Often used
    /**
     * 頂層 system prompt。穩定（cacheable）的 SYSTEM 訊息抽到這裡，model 會當系統指令處理，
     * 且穩定 prefix 有利於 Gemini 隱式快取（2.5 系列預設開啟）。null 時不序列化，行為與既往一致。
     * wire format 為 `system_instruction: { parts: [...] }`（無 role）。
     * https://ai.google.dev/gemini-api/docs/text-generation#system-instructions
     */
    @SerialName("system_instruction")
    val systemInstruction: SystemInstruction? = null
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
      val topK: Int? = null,
      /**
       * thinking model 才有用：true 時模型會回傳 thought summary parts（part.thought=true）。
       * 非 thinking model 會直接 ignore。
       * https://ai.google.dev/gemini-api/docs/thinking
       */
      val thinkingConfig: ThinkingConfig? = null
    ) {
      /**
       * thinkingBudget: -1 = dynamic（模型自行決定），0 = 關閉 thinking，>0 = 指定 token 上限。
       * includeThoughts 只有在 thinking 已啟用時才合法；對 thinking 預設關閉的 model（如 gemini-2.5-flash-lite）
       * 若硬送 includeThoughts=true 會 error: "include_thoughts is only enabled when thinking is enabled"。
       */
      @Serializable
      data class ThinkingConfig(
        val includeThoughts: Boolean? = null,
        val thinkingBudget: Int? = null
      )
    }


  }

  /**
   * 頂層 system_instruction。Gemini 只吃 `parts`（不帶 role），故獨立於 [Content]。
   */
  @Serializable
  data class SystemInstruction(val parts: List<Content.Part>)

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

fun IFunctionDeclaration.toGemini(): Gemini.FunctionDeclaration {
  return Gemini.FunctionDeclaration(
    this.name,
    this.description,
    Gemini.FunctionDeclaration.Parameters(
      "object",
      this.parameters.associate { p -> p.name to Gemini.FunctionDeclaration.Parameters.Argument(p.type, p.description) },
      this.parameters.filter { it.required }.map { it.name }.toList()
    )
  )
}
