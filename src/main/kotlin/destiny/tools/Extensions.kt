/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

import destiny.core.Descriptive
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import kotlin.reflect.KClass
import kotlin.time.measureTimedValue

inline fun <T, R : Any> Iterable<T>.firstNotNullResult(crossinline transform: (T) -> R?): R? {
  return this.asSequence().firstNotNullResult(transform)
}

inline fun <T, R : Any> Sequence<T>.firstNotNullResult(crossinline transform: (T) -> R?): R? {
  return this.mapNotNull { transform(it) }.firstOrNull()
}

private val logger = KotlinLogging.logger { }

private fun calculateNextDelay(currentDelay: Long, factor: Double, maxDelay: Long): Long {
  return (currentDelay * factor).toLong().coerceAtMost(maxDelay)
}

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
    currentDelay = calculateNextDelay(currentDelay, factor, maxDelay)
  }
  return block() // last attempt
}

suspend fun <T> retryUntilNonNull(maxRetries: Int = 3,
                                  initialDelay: Long = 100, // 0.1 second
                                  maxDelay: Long = 10000,   // 10 second
                                  factor: Double = 2.0,
                                  block: suspend () -> T?, ): T? {
  suspend fun attempt(retriesLeft: Int, currentDelay: Long, attemptCount: Int = 1): T? {
    return if (retriesLeft <= 0) {
      null
    } else {
      try {
        block() ?: run {
          logger.info { "Retry attempt #$attemptCount..." }
          delay(currentDelay)
          attempt(
            retriesLeft - 1,
            (currentDelay * factor).toLong().coerceAtMost(maxDelay),
            attemptCount + 1
          )
        }
      } catch (e: Exception) {
        logger.info { "Retry attempt #$attemptCount after exception: ${e.message}" }
        delay(currentDelay)
        attempt(
          retriesLeft - 1,
          calculateNextDelay(currentDelay, factor, maxDelay),
          attemptCount + 1
        )
      }
    }
  }
  return attempt(maxRetries, initialDelay)
}


sealed class RetryResult<out T> {
  data class Success<T>(val value: T) : RetryResult<T>()
  data class AllFailures(val exceptions: List<Exception>) : RetryResult<Nothing>()
}

suspend fun <T> retryWithExceptions(
  maxRetries: Int = 3,
  initialDelay: Long = 100, // 0.1 second
  maxDelay: Long = 10000,   // 10 second
  factor: Double = 2.0,
  block: suspend () -> T
): RetryResult<T> {
  val exceptions = mutableListOf<Exception>()

  suspend fun attempt(retriesLeft: Int, currentDelay: Long, attemptCount: Int = 1): RetryResult<T> {
    if (retriesLeft <= 0) {
      return RetryResult.AllFailures(exceptions)
    }

    return try {
      val result = block()
      RetryResult.Success(result)
    } catch (e: Exception) {
      logger.info { "Retry attempt #$attemptCount failed with exception: ${e.message}" }
      exceptions.add(e)

      delay(currentDelay)
      attempt(
        retriesLeft - 1,
        calculateNextDelay(currentDelay, factor, maxDelay),
        attemptCount + 1
      )
    }
  }

  return attempt(maxRetries, initialDelay)
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
    currentDelay =  calculateNextDelay(currentDelay, factor, maxDelay)
  }
  return block() // last attempt
}

val Throwable.stackTraceString: String
  get() {
    val stringWriter = StringWriter()
    this.printStackTrace(PrintWriter(stringWriter, true))
    return stringWriter.toString()
  }


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

inline fun <reified T : Enum<T>> Enum<T>.asDescriptive() : Descriptive {
  return object : Descriptive {
    override fun getTitle(locale: Locale): String {
      return this@asDescriptive.getTitle(locale)
    }

    override fun getDescription(locale: Locale): String {
      return this@asDescriptive.getDescription(locale)
    }
  }
}

inline fun <reified T : Enum<T>> Enum<T>.getTitle(locale: Locale): String {
  return try {
    ResourceBundle.getBundle(T::class.java.name, locale).getString("${this.name}.title")
  } catch (e: MissingResourceException) {
    this.name
  }
}

inline fun <reified T : Enum<T>> Enum<T>.getDescription(locale: Locale): String {
  return try {
    ResourceBundle.getBundle(T::class.java.name, locale).getString("${this.name}.description")
  } catch (e : MissingResourceException) {
    getTitle(locale)
  }
}

fun <T : Enum<T>> KClass<out Enum<T>>.getValues(): Array<out Enum<T>> {
  return this.java.enumConstants
}

inline fun <reified T : Enum<T>> iterator(): Iterator<T> = enumValues<T>().iterator()

fun JsonElement.toMap(): Map<String, Any> {
  logger.debug { "JsonElement.toMap : $this" }
  return when (this) {
    is JsonObject -> this.mapValues { (key, jsonElement: JsonElement) ->
      when (jsonElement) {
        is JsonPrimitive -> when {
          jsonElement.isString              -> jsonElement.content
          jsonElement.booleanOrNull != null -> jsonElement.boolean
          jsonElement.intOrNull != null     -> jsonElement.int
          jsonElement.doubleOrNull != null  -> jsonElement.double
          jsonElement.floatOrNull != null   -> jsonElement.float
          else                              -> null
        }
        is JsonObject    -> jsonElement.toMap()
        is JsonArray     -> jsonElement.map { it.toMap() }
        else             -> null
      }
    }.filter { (_, v) -> v != null }
      .mapValues { (_, v) -> v!! }

    else          -> emptyMap()
  }
}
