/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.tools

object ArrayTools {

  operator fun <T> get(ARRAY: Array<T>, index: Int): T {
    val length = ARRAY.size
    return when {
      index < 0 -> get(ARRAY, index + length)
      index >= length -> get(ARRAY, index % length)
      else -> ARRAY[index]
    }
  }
}
