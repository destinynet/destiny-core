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
                       override var pressure: Double = 1013.25,
                       override var starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN): ITransConfig

@Deprecated("config DSL 將移除：改用 TransConfig(...) 具名參數或 copy()。")
@DestinyMarker
class TransConfigBuilder : Builder<TransConfig> {

  var discCenter: Boolean = false
  var refraction: Boolean = true
  var temperature: Double = 0.0
  var pressure: Double = 1013.25
  var starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN

  override fun build(): TransConfig {
    return TransConfig(discCenter, refraction, temperature, pressure, starTypeOptions)
  }

  companion object {
    fun trans(block : TransConfigBuilder.() -> Unit = {}) : TransConfig {
      return TransConfigBuilder().apply(block).build()
    }
  }
}
