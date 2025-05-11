/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import kotlin.time.Duration

data class ProviderGroup(val preferred: ProviderModel,
                         val fallbacks: Set<ProviderModel>,
                         val fallbackDelay: Duration,
                         val postProcessors : List<IPostProcessor>) {
  init {
    require(!fallbacks.contains(preferred))
  }
}
