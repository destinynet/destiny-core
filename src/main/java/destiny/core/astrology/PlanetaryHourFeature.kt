/**
 * Created by smallufo on 2022-10-11.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.tools.AbstractCachedFeature
import destiny.tools.Feature
import javax.inject.Named


enum class PlanetaryHourType {
  ASTRO,
  CLOCK
}

data class PlanetaryHourConfig(val type: PlanetaryHourType = PlanetaryHourType.ASTRO,
                               val transConfig: TransConfig = TransConfig())

interface IPlanetaryHourFeature : Feature<PlanetaryHourConfig, PlanetaryHour?>

@Named
class PlanetaryHourFeature(private val astroHourImplMap : Map<PlanetaryHourType, IPlanetaryHour> ,
                           private val julDayResolver: JulDayResolver) : AbstractCachedFeature<PlanetaryHourConfig, PlanetaryHour?>() , IPlanetaryHourFeature {

  override val defaultConfig: PlanetaryHourConfig = PlanetaryHourConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: PlanetaryHourConfig): PlanetaryHour? {
    return astroHourImplMap[config.type]!!.getPlanetaryHour(gmtJulDay, loc, julDayResolver, config.transConfig)
  }

}
