/**
 * Created by smallufo on 2025-02-06.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Reka {

  @Serializable
  data class ChatMessage(
    /** 'human' or 'model' */
    val type: String,
    val text: String
  )


  @Serializable
  data class ChatModel(
    @SerialName("conversation_history")
    val history: List<ChatMessage>,

    @SerialName("model_name")
    val modelName: String,
    val temperature: Double = 0.9,
    @SerialName("request_output_len")
    val requestOutputLen: Int = 4096
  )

  @Serializable
  data class Response(
    val type: String, val text: String,
    @SerialName("finish_reason")
    val finishReason: String,
    @SerialName("metadata")
    val metaData: MetaData
  ) {

    @Serializable
    data class MetaData(
      @SerialName("input_tokens")
      val inputTokens: Int,
      @SerialName("generated_tokens")
      val generatedTokens: Int
    )
  }

}
