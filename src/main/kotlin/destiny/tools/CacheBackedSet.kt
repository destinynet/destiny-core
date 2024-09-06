/**
 * Created by smallufo on 2024-09-06.
 */
package destiny.tools

import javax.cache.Cache


class CacheBackedSet<E>(private val cache: Cache<E, Boolean>) : MutableSet<E> {

  override fun add(element: E): Boolean {
    cache.put(element, PRESENT)
    return true
  }



  override fun clear() {
    cache.clear()
  }

  override fun iterator(): MutableIterator<E> {
    val iter = cache.unwrap(Iterable::class.java).iterator()
    return object : MutableIterator<E> {
      private var last: E? = null

      override fun hasNext() = iter.hasNext()
      override fun next(): E {
        val entry = iter.next() as Cache.Entry<E, *>
        last = entry.key
        return entry.key
      }

      override fun remove() {
        checkNotNull(last) { "Call next() before remove()" }
        cache.remove(last)
        last = null
      }
    }
  }

  override fun remove(element: E): Boolean {
    return cache.remove(element)
  }

  override fun addAll(elements: Collection<E>): Boolean {
    return elements.fold(false) { modified, element -> add(element) || modified }
  }

  override fun removeAll(elements: Collection<E>): Boolean {
    return elements.fold(false) { acc, element -> remove(element) || acc }
  }

  override fun retainAll(elements: Collection<E>): Boolean {
    val elementsSet = elements.toSet()
    val iterator = iterator()
    var modified = false
    while (iterator.hasNext()) {
      if (iterator.next() !in elementsSet) {
        iterator.remove()
        modified = true
      }
    }
    return modified
  }

  override fun contains(element: E): Boolean {
    return cache.containsKey(element)
  }

  override fun containsAll(elements: Collection<E>): Boolean {
    return elements.all { contains(it) }
  }

  override fun isEmpty(): Boolean {
    return !iterator().hasNext()
  }

  override val size: Int
    get() = count()

  override fun toString(): String {
    return joinToString(", ", "[", "]")
  }

  private fun count(): Int {
    var count = 0
    for (element in this) {
      count++
    }
    return count
  }

  companion object {
    private const val PRESENT: Boolean = true
  }
}

