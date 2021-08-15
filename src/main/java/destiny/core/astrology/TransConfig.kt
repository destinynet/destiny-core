/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.tools.Builder
import kotlinx.serialization.Serializable


@Serializable
data class TransConfig(val discCenter: Boolean = false,
                       val refraction: Boolean = true,
                       val temperature: Double = 0.0,
                       val pressure: Double = 1013.25)

class TransConfigBuilder : Builder<TransConfig> {

  var discCenter: Boolean = false
  var refraction: Boolean = true
  var temperature: Double = 0.0
  var pressure: Double = 1013.25

  override fun build(): TransConfig {
    return TransConfig(discCenter, refraction, temperature, pressure)
  }

  companion object {
    fun trans(block : TransConfigBuilder.() -> Unit = {}) : TransConfig {
      return TransConfigBuilder().apply(block).build()
    }
  }
}
