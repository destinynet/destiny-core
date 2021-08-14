package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Feature

class HouseCuspMapFeature(private val houseCuspImpl : IHouseCusp) : Feature<HouseConfig, Map<Int, ZodiacDegree>> {
  override val key: String = "houseCuspMap"

  override val defaultConfig: HouseConfig = HouseConfig(HouseSystem.MERIDIAN, Coordinate.ECLIPTIC)

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HouseConfig): Map<Int, ZodiacDegree> {
    return houseCuspImpl.getHouseCuspMap(gmtJulDay , loc , config.houseSystem , config.coordinate)
  }
}
