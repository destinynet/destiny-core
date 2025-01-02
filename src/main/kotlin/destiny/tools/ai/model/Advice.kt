/**
 * Created by smallufo on 2024-12-25.
 */
package destiny.tools.ai.model

import destiny.tools.ai.Provider
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Advice(
  val summary: String,
  val pros: List<String> = emptyList(),
  val cons: List<String> = emptyList(),
  val actions: List<String> = emptyList(),
  var provider: Provider? = null,
  var model: String? = null
) : IDigestResponse, Serializable


@kotlinx.serialization.Serializable
data class AdviceList(
  val adviceList: List<Advice>
) : IDigestResponse {
  companion object {
    fun kSerializer(): KSerializer<AdviceList> = AdviceListSerializer
  }
}

object AdviceListSerializer : KSerializer<AdviceList> {
  private val listSerializer = ListSerializer(Advice.serializer())

  override val descriptor = listSerializer.descriptor

  override fun serialize(encoder: Encoder, value: AdviceList) {
    // 只序列化內部的 adviceList
    encoder.encodeSerializableValue(listSerializer, value.adviceList)
  }

  override fun deserialize(decoder: Decoder): AdviceList {
    // 從 JSON 中反序列化成 List<Advice>
    val adviceList = decoder.decodeSerializableValue(listSerializer)
    return AdviceList(adviceList)
  }
}
