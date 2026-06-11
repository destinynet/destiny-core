/**
 * Created by smallufo on 2024-08-19.
 */
package destiny.tools.ai

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

interface IProviderModel {
  val provider: Provider?
  val model: String?

  fun withProviderModel(provider: Provider, model: String): IProviderModel
}

@Serializable
data class ProviderModel(val provider: Provider, val model: String, @Contextual val temperature: Temperature? = null)
