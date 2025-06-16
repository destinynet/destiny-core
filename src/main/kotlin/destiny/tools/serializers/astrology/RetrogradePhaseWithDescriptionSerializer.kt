/**
 * Created by smallufo on 2025-01-27.
 */
@file:OptIn(ExperimentalSerializationApi::class)

package destiny.tools.serializers.astrology

import destiny.core.astrology.RetrogradePhase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object RetrogradePhaseWithDescriptionSerializer : KSerializer<RetrogradePhase?> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RetrogradePhase") {
    element<String?>("state") // 狀態，可以是 null
    element<String>("description") // 延伸描述
  }

  override fun serialize(encoder: Encoder, value: RetrogradePhase?) {
    val compositeEncoder = encoder.beginStructure(descriptor)
    if (value == null) {
      // 處理 null 狀態
      compositeEncoder.encodeNullableSerializableElement(descriptor, 0, String.serializer(), null)
      compositeEncoder.encodeStringElement(descriptor, 1, "Direct motion, no retrograde influence")
    } else {
      // 處理非 null 狀態
      compositeEncoder.encodeStringElement(descriptor, 0, value.name)
      val description = when (value) {
        RetrogradePhase.PREPARING    -> "Direct motion, entering retrograde shadow"
        RetrogradePhase.RETROGRADING -> "In retrograde motion"
        RetrogradePhase.LEAVING      -> "Direct motion, leaving retrograde shadow"
      }
      compositeEncoder.encodeStringElement(descriptor, 1, description)
    }
    compositeEncoder.endStructure(descriptor)
  }

  override fun deserialize(decoder: Decoder): RetrogradePhase? {
    val dec = decoder.beginStructure(descriptor)
    var state: String? = null
    while (true) {
      when (val index = dec.decodeElementIndex(descriptor)) {
        0                            -> state = dec.decodeNullableSerializableElement(descriptor, index, String.serializer())
        CompositeDecoder.DECODE_DONE -> break
      }
    }
    dec.endStructure(descriptor)
    return state?.let { RetrogradePhase.valueOf(it) } // 如果 state 為 null，返回 null
  }
}
