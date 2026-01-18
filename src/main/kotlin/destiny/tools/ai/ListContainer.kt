/**
 * Created by smallufo on 2025-08-13.
 */
package destiny.tools.ai

import destiny.tools.workflow.SegmentOutput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer


@Serializable
data class ListContainer<T>(val array: List<T>) : SegmentOutput

inline fun <reified T> listContainerSerializer(): KSerializer<ListContainer<T>> {
  return ListContainerSerializer(serializer<T>())
}

class ListContainerSerializer<T>(dataSerializer: KSerializer<T>) : KSerializer<ListContainer<T>> {

  private val listSerializer = ListSerializer(dataSerializer)

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ListContainer") {
    element("array", listSerializer.descriptor)
  }


  override fun serialize(encoder: Encoder, value: ListContainer<T>) {
    val composite = encoder.beginStructure(descriptor)
    composite.encodeSerializableElement(descriptor, 0, listSerializer, value.array)
    composite.endStructure(descriptor)
  }

  override fun deserialize(decoder: Decoder): ListContainer<T> {
    val jsonDecoder = decoder as? JsonDecoder ?: error("This serializer can only be used with JSON format.")

    val list = when (val jsonElement = jsonDecoder.decodeJsonElement()) {
      is JsonObject -> {
        val arrayElement = jsonElement["array"]
          ?: throw SerializationException("Missing 'array' key in JSON object for ListContainer")
        jsonDecoder.json.decodeFromJsonElement(listSerializer, arrayElement)
      }

      is JsonArray  -> {
        jsonDecoder.json.decodeFromJsonElement(listSerializer, jsonElement)
      }

      else          -> throw SerializationException("Expected JSON object or array for ListContainer, but got ${jsonElement::class.simpleName}")
    }
    return ListContainer(list)
  }
}
