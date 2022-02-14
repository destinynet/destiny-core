/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.tools

object ArrayTools {

  operator fun <T> get(array: Array<T>, index: Int): T {
    val length = array.size

    return if (index in 0 until length) {
      array[index]
    } else {
      val newId = (index % length)
        .let { v -> if (v < 0) v + length else v }
      array[newId]
    }
  }
}
