/**
 * Created by smallufo on 2023-04-18.
 */
package destiny.tools

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Set<Container<IAnimal>> to find which container contains [type] == Cat
 */
fun <E> Collection<E>.searchImpl(type: Type, containerClazz: Class<*>? = null): E? {
  return this.firstOrNull { each: E ->
    (each!!::class.java.genericInterfaces.firstOrNull {
      it is ParameterizedType
    } as? ParameterizedType)?.let {
      it.rawType to it.actualTypeArguments?.get(0)
    }?.let { (containerType, argumentType) ->
      if (containerClazz != null) {
        containerType == containerClazz && argumentType == type
      } else {
        argumentType == type
      }
    } ?: false

  }
}


@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Enum<T>> parseJsonToMap(json: String): Map<T, String> {
  val keyToDomain = enumValues<T>().associateBy { it.name }

  val jsonParser = Json {
    allowTrailingComma = true
  }

  return try {
    jsonParser.decodeFromString<Map<String, String>>(json).entries
      .mapNotNull { (key, value) -> keyToDomain[key]?.let { it to value } }
      .toMap()
  } catch (e: SerializationException) {
    emptyMap()
  }
}
