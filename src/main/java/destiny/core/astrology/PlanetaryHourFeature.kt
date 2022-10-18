/**
 * Created by smallufo on 2022-10-11.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import javax.inject.Named


enum class PlanetaryHourType {
  ASTRO,
  CLOCK
}

@Serializable
data class PlanetaryHourConfig(val type: PlanetaryHourType = PlanetaryHourType.ASTRO,
                               val transConfig: TransConfig = TransConfig())

@DestinyMarker
class PlanetaryHourConfigBuilder : Builder<PlanetaryHourConfig> {

  var type = PlanetaryHourType.ASTRO

  var transConfig : TransConfig = TransConfig()
  fun transConfig(block: TransConfigBuilder.() -> Unit = {}) {
    transConfig = TransConfigBuilder.trans(block)
  }


  override fun build(): PlanetaryHourConfig {
    return PlanetaryHourConfig(type, transConfig)
  }

  companion object {
    fun planetaryHour(block : PlanetaryHourConfigBuilder.() -> Unit = {}) : PlanetaryHourConfig {
      return PlanetaryHourConfigBuilder().apply(block).build()
    }
  }
}

interface IPlanetaryHourFeature : Feature<PlanetaryHourConfig, PlanetaryHour?> {

  fun getDailyPlanetaryHours(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: PlanetaryHourConfig = PlanetaryHourConfig()): List<PlanetaryHour>
}

@Named
class PlanetaryHourFeature(private val astroHourImplMap : Map<PlanetaryHourType, IPlanetaryHour> ,
                           private val julDayResolver: JulDayResolver) : AbstractCachedFeature<PlanetaryHourConfig, PlanetaryHour?>() , IPlanetaryHourFeature {

  override val defaultConfig: PlanetaryHourConfig = PlanetaryHourConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: PlanetaryHourConfig): PlanetaryHour? {
    return astroHourImplMap[config.type]!!.getPlanetaryHour(gmtJulDay, loc, julDayResolver, config.transConfig)
  }

  override fun getDailyPlanetaryHours(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: PlanetaryHourConfig): List<PlanetaryHour> {
    val lmtStart = lmt.with(ChronoField.HOUR_OF_DAY, 0).with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0).with(ChronoField.NANO_OF_SECOND, 0)

    return astroHourImplMap[config.type]!!.getDailyPlanetaryHours(lmtStart.toLocalDate(), loc, julDayResolver, config.transConfig)
  }
}
