/**
 * Created by smallufo on 2023-04-17.
 */
package destiny.core.oracles

import destiny.core.oracles.dizang.Dizang
import destiny.core.oracles.guanyin.Guanyin
import destiny.core.oracles.sixty.Sixty
import destiny.core.oracles.storm.Storm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import mu.KotlinLogging
import java.lang.reflect.ParameterizedType
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
        val oracle: IOracle<*> = oracles.searchImpl(value::class)
        logger.trace { "oracle = $oracle" }
        oracle.getIntLocator(value)
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
        val oracle = oracles.searchImpl(clause!!)
        oracle.getClause(indexFromOne)!!

      } else {
        throw RuntimeException("err")
      }
    }
  }


  companion object {
    private val logger = KotlinLogging.logger { }

    private fun Set<IOracle<*>>.searchImpl(kClass: KClass<out IClause>): IOracle<*> {

      return this.first { oracle ->
        val oracleModelClass = (oracle.javaClass.genericInterfaces.firstOrNull {
          it is ParameterizedType && it.rawType == IOracle::class.java
        } as? ParameterizedType)?.actualTypeArguments?.get(0)

        oracleModelClass != null && oracleModelClass == kClass.java
      }
    }

    val supportedClazz = setOf(Dizang::class, Guanyin::class, Sixty::class, Storm::class)
  }

}

