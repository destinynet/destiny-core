/**
 * Created by smallufo on 2023-12-31.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Gemini {

  @Serializable
  data class Content(val role: String, val parts: List<Part>?) {
    @Serializable
    data class Part(val text: String?, @SerialName("inlineData") val inlineData: InlineData? = null,
                    val functionCall: FunctionCall? = null, val functionResponse: FunctionResponse? = null) {
      @Serializable
      data class InlineData(val mimeType: String, val data: String)
      @Serializable
      data class FunctionCall(val name: String, val args: Map<String, String>) {
        val argsList : List<Pair<String,String>>
          get() = args.map { (k, v) -> k to v }.toList()
      }
      @Serializable
      data class FunctionResponse(val name : String , val response : Response) {
        @Serializable
        data class Response(val name: String, val content: String)
      }
    }
  }

  @Serializable
  data class Request(val contents: List<Content>,
                     val tools : List<Tool>? = null,
                     @SerialName("generation_config")
                     val config: Config) {
    @Serializable
    data class Config(
      /**
       * max
       * gemini-pro : 8192 ,
       * gemini-pro-vision : 2048
       */
      val maxOutputTokens: Int = 2048,
      val temperature: Double = 0.9,
      val topP: Double = 0.75,
      /**
       * Default for gemini-pro-vision: 32
       * Default for gemini-pro: none
       */
      val topK: Int = 32
    )
  }

  @Serializable
  data class Tool(@SerialName("function_declarations") val functionDeclarations: List<FunctionDeclaration>)

  @Serializable
  data class FunctionDeclaration(val name: String, val description: String, val parameters: Parameters) {
    @Serializable
    data class Parameters(val type : String, val properties : Map<String , Argument>, val required: List<String>) {
      @Serializable
      data class Argument(val type: String, val description: String)
    }
  }

  sealed class Response {
    @Serializable
    data class CandidateContainer(val candidates: List<Candidate>) : Response() {
      @Serializable
      data class Candidate(val content: Content)

      /** contents with functionCall */
      val contentsWithFunctionCall: List<Content>
        get() {
          return candidates.mapNotNull { candidate -> candidate.content.takeIf { c -> c.parts?.any { p -> p.functionCall != null } ?: false } }
        }
    }

    @Serializable
    data class ErrorContainer(val error: Error?) : Response() {
      @Serializable
      data class Error(val code: Int, val message: String, val status: String)
    }
  }
}


