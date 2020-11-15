/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

import kotlinx.coroutines.delay
import mu.KotlinLogging
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

inline fun <T, R : Any> Iterable<T>.firstNotNullResult(crossinline transform: (T) -> R?): R? {
  return this.asSequence().firstNotNullResult(transform)
}

inline fun <T, R : Any> Sequence<T>.firstNotNullResult(crossinline transform: (T) -> R?): R? {
  return this.mapNotNull { transform(it) }.firstOrNull()
}

private val logger = KotlinLogging.logger { }

/**
 * https://stackoverflow.com/a/46890009/298430
 */
suspend fun <T> retryIO(
  times: Int = Int.MAX_VALUE,
  initialDelay: Long = 100, // 0.1 second
  maxDelay: Long = 10000,   // 10 second
  factor: Double = 2.0,
  block: suspend () -> T): T {
  var currentDelay = initialDelay
  repeat(times - 1) {
    try {
      return block()
    } catch (e: IOException) {
      logger.warn("IO exception : {}", e.stackTraceString)
    }
    delay(currentDelay)
    currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
  }
  return block() // last attempt
}


/**
 * https://stackoverflow.com/a/46890009/298430
 */
suspend fun <T> retry(
  times: Int = Int.MAX_VALUE,
  initialDelay: Long = 100, // 0.1 second
  maxDelay: Long = 10000,   // 10 second
  factor: Double = 2.0,
  block: suspend () -> T): T {
  var currentDelay = initialDelay
  repeat(times - 1) {
    try {
      return block()
    } catch (e: Throwable) {
      KotlinLogging.logger { }.warn("throwable : {}", e.message)
    }
    delay(currentDelay)
    currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
  }
  return block() // last attempt
}

val Throwable.stackTraceString: String
  get() {
    val stringWriter = StringWriter()
    this.printStackTrace(PrintWriter(stringWriter, true))
    return stringWriter.toString()
  }


@ExperimentalTime
inline fun <T> measureTimed(durationFun : (kotlin.time.Duration) -> Unit, function : () -> T) : T {

  val (result , duration) = measureTimedValue(function)

  durationFun.invoke(duration)
  return result
}

/**
 * Chunking [Sequence] by [predicate]
 *
 * https://youtrack.jetbrains.com/issue/KT-41648
 *
 * Splitting sequence in chunks based on predicate
 * https://discuss.kotlinlang.org/t/splitting-sequence-in-chunks-based-on-predicate/19005
 */
fun <T> Sequence<T>.chunked(predicate: (T, T) -> Boolean): Sequence<List<T>> {
  val underlyingSequence = this
  return sequence {
    val buffer = mutableListOf<T>()
    var last: T? = null
    for (current in underlyingSequence) {
      val shouldSplit = last?.let { predicate(it, current) } ?: false
      if (shouldSplit) {
        yield(buffer.toList())
        buffer.clear()
      }
      buffer.add(current)
      last = current
    }
    if (buffer.isNotEmpty()) {
      yield(buffer)
    }
  }
}
