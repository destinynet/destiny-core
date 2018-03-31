/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.Coordinate
import destiny.astrology.HouseSystem
import destiny.astrology.ZodiacSign
import destiny.core.Descriptive
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算上升星座（八字命宮）
 */
interface IRisingSign : Descriptive {

  fun getRisingSign(gmtJulDay: Double, location: ILocation, houseSystem: HouseSystem, coordinate: Coordinate): ZodiacSign

  /**
   * @param houseSystem 分宮法，大部分不會影響上升星座。
   * 但是 [HouseSystem.VEHLOW_EQUAL] 的確會影響上升星座！
   */
  fun getRisingSign(lmt: ChronoLocalDateTime<*>, location: ILocation, houseSystem: HouseSystem, coordinate: Coordinate): ZodiacSign {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    return getRisingSign(gmtJulDay, location, houseSystem, coordinate)
  }

}
