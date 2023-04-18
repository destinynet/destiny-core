/**
 * Created by smallufo on 2023-04-17.
 */
package destiny.core.oracles

import destiny.core.oracles.dizang.Dizang
import destiny.core.oracles.guanyin.Guanyin
import destiny.core.oracles.sixty.Sixty
import destiny.core.oracles.storm.Storm
import destiny.tools.searchImpl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import javax.inject.Named
import kotlin.reflect.KClass


@Named
class IClauseSerializer(val oracles: Set<IOracle<*>>) : KSerializer<IClause> {

  override val descriptor: SerialDescriptor
    get() = buildClassSerialDescriptor("IClause") {
      element<String>("type")
      element<Int>("intMatcher")
    }

  override fun serialize(encoder: Encoder, value: IClause) {
    return encoder.encodeStructure(descriptor) {
      encodeStringElement(descriptor, 0, value::class.simpleName!!)
      encodeIntElement(descriptor, 1, run {
        oracles.searchImpl(value::class.java)
          ?.getIntLocator(value)
          ?: throw SerializationException("err when serialize")
      })
    }
  }

  override fun deserialize(decoder: Decoder): IClause {
    return decoder.decodeStructure(descriptor) {
      var clause: KClass<out IClause>? = null
      var indexFromOne: Int? = null
      loop@ while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          CompositeDecoder.DECODE_DONE -> break@loop
          0                            -> decodeStringElement(descriptor, 0).let { clazzSimpleName ->
            clause = supportedClazz.first { kClass -> kClass.simpleName == clazzSimpleName }
          }

          1                            -> indexFromOne = decodeIntElement(descriptor, 1)
          else                         -> throw SerializationException("Unexpected index $index")
        }
      }

      if (clause != null && indexFromOne != null) {
        oracles.searchImpl(clause!!.java)
          ?.getClause(indexFromOne)
          ?: throw SerializationException("err when deserialize")
      } else {
        throw SerializationException("err when deserialize")
      }
    }
  }


  companion object {
    val supportedClazz = setOf(Dizang::class, Guanyin::class, Sixty::class, Storm::class)
  }

}

