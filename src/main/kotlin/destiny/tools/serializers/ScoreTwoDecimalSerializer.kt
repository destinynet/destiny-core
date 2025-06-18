/**
 * Created by smallufo on 2025-06-17.
 */
package destiny.tools.serializers

import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.round


object ScoreTwoDecimalSerializer : KSerializer<Score> {
  // 描述符依然是 DOUBLE，因為 Score 在 JSON 中最終表現為一個數字
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Score", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: Score) {
    val rawDouble = value.value
    val roundedValue = round(rawDouble * 100) / 100.0
    encoder.encodeDouble(roundedValue)
  }

  override fun deserialize(decoder: Decoder): Score {
    return decoder.decodeDouble().toScore()
  }
}

