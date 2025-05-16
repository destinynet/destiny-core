/**
 * Created by smallufo on 2025-05-16.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


class Cerebras {

  @Serializable
  data class Response(
    val id: String,
    val choices: List<OpenAi.Response.NormalResponse.Choice>,
    val created: Long,
    val model: String,
    val `object`: String,
    val usage : OpenAi.Response.NormalResponse.Usage
  )


  data class CerebrasOptions(
    /** 0 to 1.5 */
    val temperature: Double?,
    /** 0 .. 1 */
    val topP: Double?,

    val seed: Int? = null,
  ) {
    companion object {
      fun ChatOptions.toCerebras() : CerebrasOptions {
        return CerebrasOptions(
          this.temperature?.value?.let { it * 1.5 },
          this.topP?.value
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<OpenAi.Message>,
    @Transient
    val options: CerebrasOptions? = null
  ) {
    val temperature: Double? = options?.temperature

    @SerialName("top_p")
    val topP: Double? = options?.topP

    val seed = options?.seed
  }
}
