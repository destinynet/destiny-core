/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class ZodiacSignConfig(@Serializable(with = PointSerializer::class)
                            val star: Star = Planet.SUN): java.io.Serializable

class ZodiacSignBuilder : Builder<ZodiacSignConfig> {

  var star: Star = Planet.SUN

  override fun build(): ZodiacSignConfig {
    return ZodiacSignConfig(star)
  }

  companion object {
    fun zodiacSign(block: ZodiacSignBuilder.() -> Unit = {}): ZodiacSignConfig {
      return ZodiacSignBuilder().apply(block).build()
    }
  }
}

class ZodiacSignFeature(private val zodiacSignImpl : IZodiacSign) : Feature<ZodiacSignConfig , ZodiacSignModel> {
  override val key: String = "zodiacSign"

  override val defaultConfig: ZodiacSignConfig = ZodiacSignConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: ZodiacSignConfig): ZodiacSignModel {
    return zodiacSignImpl.getSignsBetween(config.star, gmtJulDay)
  }
}
