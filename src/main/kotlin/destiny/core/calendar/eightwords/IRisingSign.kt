/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools.toGmtJulDay
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算上升星座（八字命宮）
 */
interface IRisingSign : Descriptive {

  fun getRisingSign(gmtJulDay: GmtJulDay, loc: ILocation, houseSystem: HouseSystem, coordinate: Coordinate): ZodiacSign

  /**
   * @param houseSystem 分宮法，大部分不會影響上升星座。
   * 但是 [HouseSystem.VEHLOW_EQUAL] 的確會影響上升星座！
   */
  fun getRisingSign(lmt: ChronoLocalDateTime<*>, location: ILocation, houseSystem: HouseSystem, coordinate: Coordinate): ZodiacSign {
    return getRisingSign(lmt.toGmtJulDay(location), location, houseSystem, coordinate)
  }

}
