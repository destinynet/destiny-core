/**
 * Created by smallufo on 2021-09-30.
 */
package destiny.tools.serializers

import destiny.core.calendar.eightwords.EightWordsNullable
import destiny.core.calendar.eightwords.IEightWordsNullable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object IEightWordsNullableSerializer : KSerializer<IEightWordsNullable> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ewn", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: IEightWordsNullable) {
    val string = value.run {
      listOf(
        year.stem?.indexFromOne ?: 0,
        year.branch?.indexFromOne ?: 0,
        month.stem?.indexFromOne ?: 0,
        month.branch?.indexFromOne ?: 0,
        day.stem?.indexFromOne ?: 0,
        day.branch?.indexFromOne ?: 0,
        hour.stem?.indexFromOne ?: 0,
        hour.branch?.indexFromOne ?: 0
      ).joinToString(",")
    }
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): IEightWordsNullable {
    val intList = decoder.decodeString().split(",").map { it.toInt() }
    return EightWordsNullable.getFromIntList(intList)
  }
}
