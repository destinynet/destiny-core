package destiny.core

class Circular<T>(private val elements: List<T>) : Iterable<T> {
  private var normalizedView: List<T>? = null

  val size: Int get() = elements.size

  fun get(index: Int): T = elements[index % elements.size]

  override fun iterator(): Iterator<T> = object : Iterator<T> {
    private var index = 0
    override fun hasNext() = elements.isNotEmpty()
    override fun next(): T = get(index++)
  }

  fun toList(): List<T> = elements.toList()
  fun contains(element: T): Boolean = elements.contains(element)

  fun rotateLeft(n: Int = 1): Circular<T> {
    if (size <= 1) return Circular(elements)
    val actualRotation = n % size
    val rotated = elements.subList(actualRotation, size) + elements.subList(0, actualRotation)
    return Circular(rotated)
  }

  fun rotateRight(n: Int = 1): Circular<T> = rotateLeft(size - (n % size))

  override fun toString(): String = "Circular${elements.toList()}"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Circular<*>) return false
    if (this.size != other.size) return false
    if (this.isEmpty() && other.isEmpty()) return true

    return this.normalize() == other.normalize()
  }

  override fun hashCode(): Int = normalize().hashCode()

  private fun normalize(): List<T> {
    if (normalizedView == null) {
      normalizedView = if (elements.isEmpty()) {
        emptyList()
      } else {
        val minIndex = elements.indices.minByOrNull { elements[it].hashCode() } ?: 0
        elements.subList(minIndex, elements.size) + elements.subList(0, minIndex)
      }
    }
    return normalizedView!!
  }

  private fun isEmpty() = elements.isEmpty()

  companion object {
    fun <T> of(vararg elements: T): Circular<T> = Circular(elements.toList())
  }
}
