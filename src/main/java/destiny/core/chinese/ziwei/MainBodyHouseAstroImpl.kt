/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.*
import destiny.core.calendar.Location
import destiny.core.calendar.eightwords.IRisingSign
import destiny.core.chinese.Branch

import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 利用上升星座，計算命宮
 * 利用月亮星座，計算身宮
 */
class MainBodyHouseAstroImpl(private val risingSignImpl: IRisingSign, private val starPositionImpl: IStarPosition<*>) : IMainBodyHouse, Serializable {

  override fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: Location): Pair<Branch, Branch> {
    val mainHouse = risingSignImpl.getRisingSign(lmt, loc, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    val moonPos = starPositionImpl.getPosition(Planet.MOON, lmt, loc, Centric.GEO, Coordinate.ECLIPTIC)

    val zodiacSign = ZodiacSign.getZodiacSign(moonPos.lng)

    val bodyHouse = zodiacSign.branch

    return Pair(mainHouse, bodyHouse)
  }
}
