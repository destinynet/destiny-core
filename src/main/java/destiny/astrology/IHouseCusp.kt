/**
 * @author smallufo
 * Created on 2007/5/29 at 上午 7:00:17
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IRisingSign
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/**
 * 取得宮首在「黃道」上幾度的介面<BR></BR>
 * SwissEph 的實作是 HouseCuspImpl
 */
interface IHouseCusp : IRisingSign {

  /**
   * 取得所有宮 (1~12) 的宮首在黃道幾度 , 傳回一個 length=13 的 array , array[0] 不使用, array[1] 為第 1 宮 , ... , array[12] 為第 12 宮
   */
  fun getHouseCusps(gmtJulDay: Double, loc: Location, houseSystem: HouseSystem, coordinate: Coordinate): DoubleArray

  /**
   * 取得所有宮（1~12）的宮首，是什麼星座 . 傳回一個 length=13 的 array , array[0] 不使用。
   */
  fun getHouseSigns(gmtJulDay: Double, location: Location, houseSystem: HouseSystem, coordinate: Coordinate): Map<Int , ZodiacSign> {
    val cusps = getHouseCusps(gmtJulDay, location, houseSystem, coordinate)

    return (1..12).map {
      it to ZodiacSign.getZodiacSign(cusps[it])
    }.toMap()
  }


  /** 取得「上升星座」 (分宮法/HouseSystem  或許不需要)  */
  override fun getRisingSign(gmtJulDay: Double, location: Location, houseSystem: HouseSystem, coordinate: Coordinate): ZodiacSign {
    return getHouseSigns(gmtJulDay, location, houseSystem, coordinate)[1]!!
  }


  /**
   * 取得第 index 宮的宮首在黃道幾度 , 為 1-based , 1 <= index <=12
   */
  fun getHouseCusp(index: Int, gmtJulDay: Double, location: Location, houseSystem: HouseSystem, coordinate: Coordinate): Double

  fun getHouseCusp(index: Int, lmt: ChronoLocalDateTime<*>, location: Location, houseSystem: HouseSystem, coordinate: Coordinate): Double {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    return getHouseCusp(index, gmtJulDay, location, houseSystem, coordinate)
  }

  override fun getTitle(locale: Locale): String {
    return "真實星體觀測"
  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }

}
