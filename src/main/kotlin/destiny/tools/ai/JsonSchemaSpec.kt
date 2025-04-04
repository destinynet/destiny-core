/**
 * Created by smallufo on 2025-04-04.
 */
package destiny.tools.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class JsonSchemaSpec(val name: String, val description: String?, val schema: JsonObject)


