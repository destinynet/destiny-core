/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:29:42
 */
package destiny.astrology

import destiny.core.calendar.Location
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/**
 * 2015-06-11 重寫此介面，讓此介面成為 immutable
 */
interface IHoroscope {

  val defaultPoints: Set<Point>
    get() {
      return setOf(
        *Planet.values ,
        *Asteroid.values ,
        *Hamburger.values ,
        *FixedStar.values ,
        *LunarNode.mean_values
      )
    }

  fun getDefaultPoints(nodeType: NodeType): Set<Point> {
    val pointSet = HashSet<Point>()
    pointSet.addAll(Arrays.asList(*Planet.values))
    pointSet.addAll(Arrays.asList(*Asteroid.values))
    pointSet.addAll(Arrays.asList(*Hamburger.values))
    pointSet.addAll(Arrays.asList(*FixedStar.values))
    when (nodeType) {
      NodeType.MEAN -> pointSet.addAll(Arrays.asList(*LunarNode.mean_values))
      NodeType.TRUE -> pointSet.addAll(Arrays.asList(*LunarNode.true_values))
    }
    return pointSet
  }

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: Location,
                   points: Collection<Point>,
                   houseSystem: HouseSystem,
                   centric: Centric,
                   coordinate: Coordinate,
                   temperature: Double, pressure: Double): Horoscope


  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: Location,
                   houseSystem: HouseSystem,
                   centric: Centric,
                   coordinate: Coordinate): Horoscope {
    return getHoroscope(lmt, loc, defaultPoints, houseSystem, centric, coordinate, 0.0, 1013.25)
  }

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: Location,
                   houseSystem: HouseSystem,
                   centric: Centric,
                   coordinate: Coordinate,
                   nodeType: NodeType): Horoscope {
    return getHoroscope(lmt, loc, getDefaultPoints(nodeType), houseSystem, centric, coordinate, 0.0, 1013.25)
  }
}
