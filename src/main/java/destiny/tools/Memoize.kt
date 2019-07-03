package destiny.tools

/**
 * https://medium.com/@JorgeCastilloPr/kotlin-purity-and-function-memoization-b12ab35d70a5
 * http://jorgecastillo.dev/kotlin-purity-and-function-memoization
 */
class Memoize<in T, out R>(val f: (T) -> R) : (T) -> R {
  private val values = mutableMapOf<T, R>()
  override fun invoke(x: T): R {
    return values.getOrPut(x) { f(x) }
  }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize(this)