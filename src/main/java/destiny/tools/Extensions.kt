/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

inline fun <T, R : Any> Iterable<T>.firstNotNullResult(transform: (T) -> R?): R? {
  for (element in this) {
    val result = transform(element)
    if (result != null) return result
  }
  return null
}