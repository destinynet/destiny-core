/**
 * Created by smallufo on 2021-04-30.
 */
package destiny.tools

class MutableStack<E>(vararg items: E) {

  private val elements = items.toMutableList()

  fun push(element: E) = elements.add(element)

  fun peek(): E = elements.last()

  fun pop(): E = elements.removeAt(elements.size - 1)

  fun isEmpty() = elements.isEmpty()

  fun size() = elements.size

  override fun toString() = "MutableStack(${elements.joinToString()})"
}

fun <E> mutableStackOf(vararg elements: E) = MutableStack(*elements)
