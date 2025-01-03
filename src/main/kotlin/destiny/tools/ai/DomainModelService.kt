/**
 * Created by smallufo on 2024-08-19.
 */
package destiny.tools.ai

import destiny.tools.ai.model.Domain
import destiny.tools.config.Holder
import jakarta.inject.Named
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ProviderModel(val provider: Provider, val model: String)

data class DomainLanguage(val domain: Domain, val language: String?)

object ProviderModelListSerializer : KSerializer<List<ProviderModel>> {
  override val descriptor: SerialDescriptor = ListSerializer(String.serializer()).descriptor

  override fun serialize(encoder: Encoder, value: List<ProviderModel>) {
    val stringList = value.map { "${it.provider} : ${it.model}" }
    ListSerializer(String.serializer()).serialize(encoder, stringList)
  }

  override fun deserialize(decoder: Decoder): List<ProviderModel> {
    val stringList = ListSerializer(String.serializer()).deserialize(decoder)
    return stringList.map { str ->
      val (provider, model) = str.split(":", limit = 2)
      ProviderModel(Provider.valueOf(provider.trim()), model.trim())
    }
  }
}

@Serializable
data class DomainMapping(val domain: Domain, val language: String?,
                         @Serializable(with = ProviderModelListSerializer::class)
                         var models: List<ProviderModel>
)

@Serializable
data class DomainModelConfig(val mappings: List<DomainMapping>)

interface IDomainModelService {
  fun findImpl(provider: Provider): IChatCompletion
  fun getProviderModel(domain: Domain, language: String? = null): ProviderModel
}

@Named
class DomainModelService(
  private val holder: Holder<Map<DomainLanguage, List<ProviderModel>>>,
  private val chatCompletions : Set<IChatCompletion>
): IDomainModelService {

  val allProviderModels: List<ProviderModel>
    get() = holder.getConfig().values.flatten().toSet().toList()

  override fun findImpl(provider: Provider): IChatCompletion {
    return chatCompletions.first { it.provider == provider.name }
  }

  private fun byDomain(domain: Domain): ProviderModel {
    return holder.getConfig().entries
      .filter { it.key.domain == domain }
      .flatMap { it.value }
      .randomOrNull() ?: allProviderModels.random()
  }

  override fun getProviderModel(domain: Domain, language: String?): ProviderModel {
    return if (language == null) {
      byDomain(domain)
    } else {

      // 先搜尋 domain / language 完全符合
      holder.getConfig().entries
        .filter { it.key.domain == domain && it.key.language == language }
        .flatMap { it.value }
        .randomOrNull()
        ?: run {
          // 再搜尋 domain 符合， language null
          holder.getConfig().entries.filter { it.key.domain == domain && it.key.language == null }.flatMap { it.value }.randomOrNull()
        } ?: run {
          // 最後再去此 domain 的 provider 整合去找
          byDomain(domain)
        }
    }
  }
}
