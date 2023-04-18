/**
 * Created by smallufo on 2023-04-18.
 */
package destiny.tools

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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
