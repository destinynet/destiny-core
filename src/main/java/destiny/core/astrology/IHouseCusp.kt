/**
 * @author smallufo
 * Created on 2007/5/29 at 上午 7:00:17
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IRisingSign
import destiny.core.calendar.eightwords.RisingSignConfig
import destiny.core.calendar.eightwords.toString
import java.util.*

/**
 * 取得宮首在「黃道」上幾度的介面<BR></BR>
 * SwissEph 的實作是 HouseCuspImpl
 */
interface IHouseCusp : IRisingSign {

  /**
   * 取得所有宮 (1~12) 的宮首在黃道幾度 , 傳回一個 length=13 的 list , list[0] 不使用, list[1] 為第 1 宮 , ... , list[12] 為第 12 宮
   */
  fun getHouseCusps(gmtJulDay: GmtJulDay, loc: ILocation, houseSystem: HouseSystem, coordinate: Coordinate = Coordinate.ECLIPTIC): List<ZodiacDegree>

  /**
   * 承上， 取得所有宮 (1~12) 的宮首在黃道幾度 , 傳回一個 Map , key 為 1~12 , value 為 [Coordinate] 度數 (default 黃道)
   */
  fun getHouseCuspMap(gmtJulDay: GmtJulDay, loc: ILocation, houseSystem: HouseSystem, coordinate: Coordinate = Coordinate.ECLIPTIC): Map<Int , ZodiacDegree> {
    return getHouseCusps(gmtJulDay, loc, houseSystem, coordinate)
      .drop(1)
      .mapIndexed { index, zodiacDegree ->
        val houseIndex = index+1
        houseIndex to zodiacDegree
      }.toMap()
  }

  /**
   * 取得所有宮（1~12）的宮首，是什麼星座 , 以及宮首在該星座的度數
   */
  fun getHouseSignsAndDegrees(gmtJulDay: GmtJulDay, location: ILocation, houseSystem: HouseSystem, coordinate: Coordinate = Coordinate.ECLIPTIC): Map<Int , Pair<ZodiacSign , Double>> {
    return getHouseCuspMap(gmtJulDay, location, houseSystem, coordinate).map { (houseIndex , zodiacDegree) ->
      houseIndex to zodiacDegree.signDegree
    }.toMap()
  }

  /**
   * 承上，只取星座
   * 取得所有宮（1~12）的宮首，是什麼星座
   */
  fun getHouseSigns(gmtJulDay: GmtJulDay, location: ILocation, houseSystem: HouseSystem, coordinate: Coordinate = Coordinate.ECLIPTIC): Map<Int , ZodiacSign> {
    return getHouseSignsAndDegrees(gmtJulDay, location, houseSystem, coordinate).map { (houseIndex , pair) ->
      houseIndex to pair.first
    }.toMap()
  }


  /** 取得「上升星座」 (分宮法/HouseSystem  或許不需要)  */
  override fun getRisingSign(gmtJulDay: GmtJulDay, location: ILocation, houseSystem: HouseSystem, coordinate: Coordinate): ZodiacSign {
    return getHouseSigns(gmtJulDay, location, houseSystem, coordinate).getValue(1)
  }

  override fun toString(locale: Locale): String {
    return RisingSignConfig.Impl.HouseCusp.toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }

}
