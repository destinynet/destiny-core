/**
 * Created by smallufo on 2023-04-18.
 */
package destiny.tools

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URLConnection
import java.time.LocalDateTime
import kotlin.math.floor
import kotlin.math.pow
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.*

/**
 * Set<Container<IAnimal>> to find which container contains [type] == Cat
 */
fun <E> Collection<E>.searchImpl(type: Type, containerClazz: Class<*>? = null): E? {
  return this.firstOrNull { each: E ->
    (each!!::class.java.genericInterfaces.firstOrNull {
      it is ParameterizedType
    } as? ParameterizedType)?.let {
      it.rawType to it.actualTypeArguments[0]
    }?.let { (containerType, argumentType) ->
      if (containerClazz != null) {
        containerType == containerClazz && argumentType == type
      } else {
        argumentType == type
      }
    } ?: false
  }
}

/**
 * Collection<Clazz extends Super<T>> to find matching element based on generic superclass type argument
 * @param type The type to search for in the generic superclass
 * @return The first element with matching generic superclass type argument, or null if none found
 */
fun <T> Collection<T>.searchByGenericSuperclass(type: Type): T? {
  return this.firstOrNull { element ->
    runCatching {
      element!!::class.java.genericSuperclass
        .takeIf { it is ParameterizedType }
        ?.let { it as ParameterizedType }
        ?.actualTypeArguments
        ?.firstOrNull() == type
    }.getOrNull() ?: false
  }
}

fun <T : Any> Collection<T>.searchByGenericSuperclassStrict(type: Type): T? {
  return this.firstOrNull { element ->
    runCatching {
      when (val superclass = element::class.java.genericSuperclass) {
        is ParameterizedType -> superclass.actualTypeArguments.firstOrNull() == type
        else                 -> false
      }
    }.getOrNull() ?: false
  }
}


@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Enum<T>> parseJsonToMap(json: String): Map<T, String> {
  val keyToDomain = enumValues<T>().associateBy { it.name }

  val jsonParser = Json {
    allowTrailingComma = true
  }

  return try {
    jsonParser.decodeFromString<Map<String, String>>(json).entries
      .mapNotNull { (key, value) -> keyToDomain[key]?.let { it to value } }
      .toMap()
  } catch (e: SerializationException) {
    emptyMap()
  }
}

// 將 Java Type 轉換成 Kotlin KType
fun Type.toKType(): KType {
  return when (this) {
    is Class<*> -> this.kotlin.createType()
    is ParameterizedType -> {
      val rawClass = this.rawType as Class<*>
      val kClass = rawClass.kotlin
      val args = this.actualTypeArguments.map { KTypeProjection.invariant(it.toKType()) }
      kClass.createType(args)
    }
    is java.lang.reflect.WildcardType -> {
      // An unbounded wildcard '?' has an upper bound of `Object`.
      // A bounded wildcard '? extends Foo' has an upper bound of `Foo`.
      // We map to the KType of the upper bound.
      val upperBound = this.upperBounds.firstOrNull() ?: Any::class.java
      upperBound.toKType()
    }
    else -> throw IllegalArgumentException("Unsupported type: $this of type ${this.javaClass}")
  }
}

fun Duration.truncate(unit: DurationUnit): Duration {
  return toComponents { days: Long, hours: Int, minutes: Int, seconds: Int, nanoseconds: Int ->
    when (unit) {
      NANOSECONDS  -> this // there's no smaller unit than NANOSECONDS, so just return the current Duration
      MICROSECONDS -> days.days + hours.hours + minutes.minutes + seconds.seconds + nanoseconds.nanoseconds.inWholeSeconds.seconds + nanoseconds.nanoseconds.inWholeMicroseconds.microseconds
      MILLISECONDS -> days.days + hours.hours + minutes.minutes + seconds.seconds + nanoseconds.nanoseconds.inWholeSeconds.seconds + nanoseconds.nanoseconds.inWholeMilliseconds.milliseconds
      SECONDS      -> days.days + hours.hours + minutes.minutes + seconds.seconds
      MINUTES      -> days.days + hours.hours + minutes.minutes
      HOURS        -> days.days + hours.hours
      DAYS         -> days.days
    }
  }
}

fun Double.round(epsilon: Double = 1e-5): Double {
  val nearest = kotlin.math.round(this)
  return if (kotlin.math.abs(this - nearest) < epsilon) nearest else this
}

fun Double.truncate(decimals: Int): Double {
  require(decimals >= 0) { "Decimal places must be non-negative" }
  val factor = 10.0.pow(decimals)
  return floor(this * factor) / factor
}

private const val MAX_PRECISION = 10
private val POW10 = DoubleArray(MAX_PRECISION + 1) { i -> 10.0.pow(i) }
private val FORMAT_TEMPLATES = Array(MAX_PRECISION + 1) { i -> "%.${i}f" }


fun Double.truncateToString(n: Int, epsilon: Double = 1e-5): String {
  require(n in 0..MAX_PRECISION)
  val factor = POW10[n]
  val scaled = this * factor
  val rounded = kotlin.math.round(scaled)
  val result = if (kotlin.math.abs(scaled - rounded) < epsilon) {
    rounded / factor
  } else {
    floor(scaled) / factor
  }
  return FORMAT_TEMPLATES[n].format(result)
}

fun LocalDateTime.roundToNearestSecond(): LocalDateTime {
  val nano = this.nano
  return when {
    nano < 1_000_000   -> this.withNano(0)  // 小於 1ms，直接捨去
    nano > 999_000_000 -> this.plusSeconds(1).withNano(0)  // 幾乎整秒，自動進位
    else               -> this  // 其餘保留原樣
  }
}

fun <K,V> Map<K,V>.reverse(): Map<V, K> = this.entries.associate { it.value to it.key }

fun gcd(a: Double, b: Double): Double {
  var x = a
  var y = b
  while (y > 1e-6) { // 誤差容忍
    val temp = y
    y = x % y
    x = temp
  }
  return x
}

fun ByteArray.getMimeType() : String {
  return URLConnection.guessContentTypeFromStream(this.inputStream()) ?: "application/octet-stream"
}
