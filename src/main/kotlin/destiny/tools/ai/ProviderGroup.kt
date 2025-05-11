/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import kotlin.time.Duration

data class ProviderGroup(val preferred: ProviderModel,
                         val fallbacks: Set<ProviderModel>,
                         val preferredWait: Duration,     // 專指等待 preferred 的時間
                         val modelTimeout: Duration,      // 傳給每個 impl.chatComplete 的 timeout
                         val postProcessors : List<IPostProcessor>) {
  init {
    require(!fallbacks.contains(preferred))
  }
}
