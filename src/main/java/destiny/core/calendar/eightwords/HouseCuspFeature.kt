/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.IHouseCusp
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class HouseCuspConfig(
  val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  val coordinate: Coordinate = Coordinate.ECLIPTIC
)

//@DestinyMarker
//class HouseCuspConfigBuilder : Builder<HouseCuspConfig> {
//  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
//  var coordinate: Coordinate = Coordinate.ECLIPTIC
//
//  override fun build(): HouseCuspConfig {
//    return HouseCuspConfig(houseSystem, coordinate)
//  }
//
//  companion object {
//    fun houseCusp(block: HouseCuspConfigBuilder.() -> Unit = {}): HouseCuspConfig {
//      return HouseCuspConfigBuilder().apply(block).build()
//    }
//  }
//}

class HouseCuspFeature(private val houseCuspImpl: IHouseCusp) : Feature<HouseCuspConfig, ZodiacSign> {

  override val key: String = "houseCusp"

  override val defaultConfig: HouseCuspConfig = HouseCuspConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HouseCuspConfig): ZodiacSign {
    return houseCuspImpl.getRisingSign(gmtJulDay, loc, config.houseSystem, config.coordinate)
  }

}
