/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.calendar.eightwords.ITransConfig
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable


@Serializable
data class TransConfig(override var discCenter: Boolean = false,
                       override var refraction: Boolean = true,
                       override var temperature: Double = 0.0,
                       override var pressure: Double = 1013.25): ITransConfig

@DestinyMarker
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
