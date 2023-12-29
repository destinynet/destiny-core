/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai

import kotlinx.serialization.Serializable


@Serializable
data class OpenAiFun(
  val type: String,
  val function: Function
) {
  @Serializable
  data class Function(
    val name: String,
    val description: String,
    val parameters: Parameters
  ) {
    @Serializable
    data class Parameters(
      val type: String,
      val properties: Map<String, Argument>,
      val required: List<String>
    ) {
      @Serializable
      data class Argument(
        val type: String,
        val enum: List<String>? = null,
        val description: String
      )
    }
  }
}





