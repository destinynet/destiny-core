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
      it.rawType to it.actualTypeArguments[0]
    }?.let { (containerType, argumentType) ->
      if (containerClazz != null) {
        containerType == containerClazz && argumentType == type
      } else {
        argumentType == type
      }
    } ?: false
  }
}

/**
 * Collection<Clazz extends Super<T>> to find matching element based on generic superclass type argument
 * @param type The type to search for in the generic superclass
 * @return The first element with matching generic superclass type argument, or null if none found
 */
fun <T> Collection<T>.searchByGenericSuperclass(type: Type): T? {
  return this.firstOrNull { element ->
    runCatching {
      element!!::class.java.genericSuperclass
        .takeIf { it is ParameterizedType }
        ?.let { it as ParameterizedType }
        ?.actualTypeArguments
        ?.firstOrNull() == type
    }.getOrNull() ?: false
  }
}

fun <T : Any> Collection<T>.searchByGenericSuperclassStrict(type: Type): T? {
  return this.firstOrNull { element ->
    runCatching {
      when (val superclass = element::class.java.genericSuperclass) {
        is ParameterizedType -> superclass.actualTypeArguments.firstOrNull() == type
        else                 -> false
      }
    }.getOrNull() ?: false
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
