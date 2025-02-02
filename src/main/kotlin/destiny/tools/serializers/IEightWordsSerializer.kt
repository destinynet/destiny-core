/**
 * Created by smallufo on 2025-02-03.
 */
package destiny.tools.serializers

import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.StemBranch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*


object IEightWordsSerializer : KSerializer<IEightWords> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IEightWords") {
    element<StemBranch>("year")
    element<StemBranch>("month")
    element<StemBranch>("day")
    element<StemBranch>("hour")
  }


  override fun serialize(encoder: Encoder, value: IEightWords) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, IStemBranchSerializer, value.year)
      encodeSerializableElement(descriptor, 1, IStemBranchSerializer, value.month)
      encodeSerializableElement(descriptor, 2, IStemBranchSerializer, value.day)
      encodeSerializableElement(descriptor, 3, IStemBranchSerializer, value.hour)
    }
  }

  override fun deserialize(decoder: Decoder): IEightWords {
    var year: StemBranch? = null
    var month: StemBranch? = null
    var day: StemBranch? = null
    var hour: StemBranch? = null
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> year = decodeSerializableElement(descriptor, 0, IStemBranchSerializer) as StemBranch
          1                            -> month = decodeSerializableElement(descriptor, 1, IStemBranchSerializer) as StemBranch
          2                            -> day = decodeSerializableElement(descriptor, 2, IStemBranchSerializer) as StemBranch
          3                            -> hour = decodeSerializableElement(descriptor, 3, IStemBranchSerializer) as StemBranch
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    return EightWords(year!!, month!!, day!!, hour!!)
  }

}
