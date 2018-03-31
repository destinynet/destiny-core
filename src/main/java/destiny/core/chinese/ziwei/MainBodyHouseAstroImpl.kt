/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IRisingSign
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 利用上升星座，計算命宮
 * 利用月亮星座，計算身宮
 */
class MainBodyHouseAstroImpl(private val risingSignImpl: IRisingSign, private val starPositionImpl: IStarPosition<*>) : IMainBodyHouse, Serializable {

  /**
   * 命宮、身宮 、以及「最後要給主星所使用的月數 (若為占星算法，此值為空) 」
   * 占星算法，取上升、月亮 為命宮、身宮， 不會需要「月數」
   * */
  override fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: ILocation): Triple<Branch, Branch , Int?> {
    val mainHouse = risingSignImpl.getRisingSign(lmt, loc, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    val moonPos = starPositionImpl.getPosition(Planet.MOON, lmt, loc, Centric.GEO, Coordinate.ECLIPTIC)

    val zodiacSign = ZodiacSign.getZodiacSign(moonPos.lng)

    val bodyHouse = zodiacSign.branch

    return Triple(mainHouse, bodyHouse , null)
  }
}
