/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class RiseTransConfig(@Serializable(with = PointSerializer::class)
                           val star: Star = Planet.SUN,
                           val transPoint: TransPoint = TransPoint.RISING,
                           val discCenter: Boolean = false,
                           val refraction: Boolean = true,
                           val temperature: Double = 0.0,
                           val pressure: Double = 1013.25)

@DestinyMarker
class RiseTransConfigBuilder : Builder<RiseTransConfig> {

  var star: Star = Planet.SUN
  var transPoint: TransPoint = TransPoint.RISING
  var discCenter: Boolean = false
  var refraction: Boolean = true
  var temperature: Double = 0.0
  var pressure: Double = 1013.25

  override fun build(): RiseTransConfig {
    return RiseTransConfig(star, transPoint, discCenter, refraction, temperature, pressure)
  }

  companion object {
    fun riseTrans(block : RiseTransConfigBuilder.() -> Unit ={}) : RiseTransConfig {
      return RiseTransConfigBuilder().apply(block).build()
    }
  }
}

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 */
class RiseTransFeature(val impl : IRiseTrans) : Feature<RiseTransConfig, GmtJulDay?> {

  override val key: String = "riseTrans"

  override val defaultConfig: RiseTransConfig = RiseTransConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: RiseTransConfig): GmtJulDay? {
    return impl.getGmtTransJulDay(gmtJulDay , config.star , config.transPoint , loc , config.discCenter , config.refraction , config.temperature , config.pressure)
  }
}
