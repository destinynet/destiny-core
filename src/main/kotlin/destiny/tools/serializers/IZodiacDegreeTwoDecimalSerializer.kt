package destiny.tools.serializers

import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.getTitle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.DecimalFormat
import java.util.*

object IZodiacDegreeTwoDecimalSerializer : KSerializer<IZodiacDegree>{
  private val df = DecimalFormat("0.00")

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IZodiacDegree") {
    element<String>("zodiac")
    element<String>("signDegree")
  }

  override fun serialize(encoder: Encoder, value: IZodiacDegree) {
    val composite = encoder.beginStructure(descriptor)
    val zDegRounded = df.format(value.zDeg)

    val (sign, signDeg) = value.signDegree
    val signDegree = "${sign.getTitle(Locale.ENGLISH)}/${df.format(signDeg)}"
    composite.encodeStringElement(descriptor, 0, zDegRounded)
    composite.encodeStringElement(descriptor, 1, signDegree)
    composite.endStructure(descriptor)
  }

  override fun deserialize(decoder: Decoder): IZodiacDegree {
    val dec = decoder.beginStructure(descriptor)

    var zDeg: Double? = null
    loop@ while (true) {
      when (val index = dec.decodeElementIndex(descriptor)) {
        0                            -> zDeg = dec.decodeStringElement(descriptor, 0).toDouble()
        1                            -> dec.decodeStringElement(descriptor, 1) // 忽略 signDegree
        CompositeDecoder.DECODE_DONE -> break@loop
        else                         -> throw SerializationException("Unknown index $index")
      }
    }

    dec.endStructure(descriptor)
    return zDeg!!.toZodiacDegree()
  }
}
