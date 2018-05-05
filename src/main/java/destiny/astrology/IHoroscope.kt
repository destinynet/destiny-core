/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:29:42
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

/**
 * 2015-06-11 重寫此介面，讓此介面成為 immutable
 */
interface IHoroscope {

  val defaultPoints: Set<Point>
    get() = setOf(
      *Planets.array,
      *Asteroids.array,
      *Hamburgers.array,
      *FixedStars.array,
      *LunarNodes.meanArray
    )


  fun getDefaultPoints(nodeType: NodeType): Set<Point> {
    val pointSet = mutableSetOf<Point>()
    pointSet.addAll(listOf(*Planets.array))
    pointSet.addAll(listOf(*Asteroids.array))
    pointSet.addAll(listOf(*Hamburgers.array))
    pointSet.addAll(listOf(*FixedStars.array))
    when (nodeType) {
      NodeType.MEAN -> pointSet.addAll(listOf(*LunarNodes.meanArray))
      NodeType.TRUE -> pointSet.addAll(listOf(*LunarNodes.trueArray))
    }
    return pointSet
  }

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                   points: Collection<Point>,
                   houseSystem: HouseSystem,
                   centric: Centric,
                   coordinate: Coordinate,
                   temperature: Double?=0.0, pressure: Double?=1013.25): Horoscope


  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                   houseSystem: HouseSystem,
                   centric: Centric,
                   coordinate: Coordinate): Horoscope {
    return getHoroscope(lmt, loc, defaultPoints, houseSystem, centric, coordinate, 0.0, 1013.25)
  }

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                   houseSystem: HouseSystem,
                   centric: Centric,
                   coordinate: Coordinate,
                   nodeType: NodeType): Horoscope {
    return getHoroscope(lmt, loc, getDefaultPoints(nodeType), houseSystem, centric, coordinate, 0.0, 1013.25)
  }
}
