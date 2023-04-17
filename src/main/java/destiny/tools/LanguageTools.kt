/**
 * Created by smallufo on 2023-04-18.
 */
package destiny.tools

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun <E> Collection<E>.searchImpl(type: Type, clazz: Class<*>): E? {
  return this.firstOrNull { each: E ->
    val eachClass = (each!!::class.java.genericInterfaces.firstOrNull {
      it is ParameterizedType && it.rawType == clazz
    } as? ParameterizedType)?.actualTypeArguments?.get(0)
    eachClass != null && eachClass == type
  }
}
