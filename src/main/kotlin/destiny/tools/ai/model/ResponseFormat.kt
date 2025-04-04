/**
 * Created by smallufo on 2025-04-04.
 */
package destiny.tools.ai.model

import destiny.tools.ai.JsonSchemaSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class ResponseFormat {

  @Serializable
  @SerialName("text")
  data object TextResponse : ResponseFormat()

  @Serializable
  @SerialName("json_schema")
  data class JsonSchemaResponse(@SerialName("json_schema") val spec: JsonSchemaSpec) : ResponseFormat()
}

