/**
 * Created by smallufo on 2025-04-04.
 */
package destiny.tools.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class JsonSchemaSpec(
  /**
   * pattern '^[a-zA-Z0-9_-]+$'
   */
  val name: String,
  val description: String?, val schema: JsonObject)


