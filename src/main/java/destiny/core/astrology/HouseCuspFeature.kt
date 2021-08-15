package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Feature

class HouseCuspFeature(private val houseCuspImpl : IHouseCusp) : Feature<HouseConfig, Map<Int, ZodiacDegree>> {
  override val key: String = "houseCusp"

  override val defaultConfig: HouseConfig = HouseConfig(HouseSystem.MERIDIAN, Coordinate.ECLIPTIC)

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HouseConfig): Map<Int, ZodiacDegree> {
    return houseCuspImpl.getHouseCuspMap(gmtJulDay , loc , config.houseSystem , config.coordinate)
  }

  /**
   * 取得所有宮（1~12）的宮首，是什麼星座 , 以及宮首在該星座的度數
   */
  fun getHouseSignsAndDegrees(gmtJulDay: GmtJulDay, loc: ILocation, config: HouseConfig): Map<Int , Pair<ZodiacSign , Double>> {
    return getModel(gmtJulDay, loc, config).map { (houseIndex , zodiacDegree) ->
      houseIndex to zodiacDegree.signDegree
    }.toMap()
  }

  /**
   * 承上，只取星座
   * 取得所有宮（1~12）的宮首，是什麼星座
   */
  fun getHouseSigns(gmtJulDay: GmtJulDay, loc: ILocation, config: HouseConfig): Map<Int , ZodiacSign> {
    return getHouseSignsAndDegrees(gmtJulDay, loc, config).map { (houseIndex , pair) ->
      houseIndex to pair.first
    }.toMap()
  }

  /** 取得「上升星座」 (分宮法/HouseSystem  或許不需要)  */
  fun getRisingSign(gmtJulDay: GmtJulDay, location: ILocation, config: HouseConfig): ZodiacSign {
    return getHouseSigns(gmtJulDay, location, config).getValue(1)
  }
}
