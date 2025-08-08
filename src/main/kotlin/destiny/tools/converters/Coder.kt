/**
 * Created by smallufo on 2019-01-23.
 */
package destiny.tools.converters

import destiny.tools.KotlinLogging

interface Encoder<T> {
  fun encode(instance: T): String
}

interface Decoder<T> {

  fun tryDecode(encodedString: String): T?

  fun decode(encodedString : String) : T? {
    return try {
      tryDecode(encodedString)
    } catch (e: Exception) {
      logger.error(e) { "could not decode: ${encodedString.take(30)}...(len=${encodedString.length})" }
      null
    }
  }

  companion object {
    private val logger = KotlinLogging.logger {  }
  }
}

/** [Encoder] + [Decoder] */
interface Coder<T> : Encoder<T> , Decoder<T>
