package destiny.tools.ai

import kotlinx.serialization.Serializable

@Serializable
data class InputSchema(val type: String = "object", val properties: Map<String, Property>, val required: List<String>) {
  @Serializable
  data class Property(val type: String, val description: String)
}
