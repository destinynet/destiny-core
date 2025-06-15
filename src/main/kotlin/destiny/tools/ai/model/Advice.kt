/**
 * Created by smallufo on 2024-12-25.
 */
package destiny.tools.ai.model

import destiny.tools.ai.IProviderModel
import destiny.tools.ai.Provider
import kotlinx.serialization.Serializable

@Serializable
data class Advice(
  val summary: String,
  val pros: List<String> = emptyList(),
  val cons: List<String> = emptyList(),
  val actions: List<String> = emptyList(),
  override val provider: Provider? = null,
  override val model: String? = null,
) : IProviderModel {
  override fun withProviderModel(provider: Provider, model: String): IProviderModel {
    return this.copy(provider = provider, model = model)
  }
}
