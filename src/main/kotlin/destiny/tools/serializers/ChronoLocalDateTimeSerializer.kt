/**
 * Created by Claude on 2026-04-08.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime

/**
 * Serializer for `ChronoLocalDateTime<*>`. In this codebase the only
 * realistic implementation is [LocalDateTime], so we delegate both
 * directions to [LocalDateTimeSerializer]. If a non-LocalDateTime
 * subclass appears at runtime we throw rather than silently lose data.
 */
@Suppress("UNCHECKED_CAST")
object ChronoLocalDateTimeSerializer : KSerializer<ChronoLocalDateTime<*>> {

  override val descriptor: SerialDescriptor = LocalDateTimeSerializer.descriptor

  override fun serialize(encoder: Encoder, value: ChronoLocalDateTime<*>) {
    val ldt = value as? LocalDateTime
      ?: throw IllegalArgumentException(
        "ChronoLocalDateTimeSerializer only supports java.time.LocalDateTime, got ${value::class.qualifiedName}"
      )
    LocalDateTimeSerializer.serialize(encoder, ldt)
  }

  override fun deserialize(decoder: Decoder): ChronoLocalDateTime<*> {
    return LocalDateTimeSerializer.deserialize(decoder)
  }
}
