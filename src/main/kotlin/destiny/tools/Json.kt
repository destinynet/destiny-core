/**
 * Created by smallufo on 2024-10-18.
 */
package destiny.tools

import kotlinx.serialization.json.*


object Json {

  private val logger = KotlinLogging.logger {}

  fun JsonElement.toMap(): Map<String, Any> {
    logger.info { "JsonElement.toMap : $this" }
    return when (this) {
      is JsonObject -> this.mapValues { (_, jsonElement: JsonElement) ->
        when (jsonElement) {
          is JsonPrimitive -> when {
            jsonElement.isString              -> jsonElement.content
            jsonElement.booleanOrNull != null -> jsonElement.boolean
            jsonElement.intOrNull != null     -> jsonElement.int
            jsonElement.doubleOrNull != null  -> jsonElement.double
            jsonElement.floatOrNull != null   -> jsonElement.float
            else                              -> null
          }

          is JsonObject    -> jsonElement.toMap()
          is JsonArray     -> jsonElement.map { it.toMap() }
          else             -> null
        }
      }.filter { (_, v) -> v != null }
        .mapValues { (_, v) -> v!! }

      else          -> emptyMap()
    }
  }
}
