/**
 * Created by smallufo on 2018-05-07.
 */
package destiny.astrology

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IHoroscopeContext {

  fun getHoroscope(gmtJulDay: Double, loc: ILocation, gender: Gender, temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoro

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoro {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, gender, temperature, pressure)
  }

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender): IHoro {
    return getHoroscope(lmt, loc, gender, null, null)
  }
}

/**
 * to replace [IHoroscope]
 */
class HoroscopeContext(
  val points: Collection<Point>,
  val houseSystem: HouseSystem,
  val centric: Centric,
  val coordinate: Coordinate,
  val starPositionWithAzimuthImpl: IStarPositionWithAzimuth,
  val houseCuspImpl: IHouseCusp) : IHoroscopeContext, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun getHoroscope(gmtJulDay: Double,
                            loc: ILocation,
                            gender: Gender,
                            temperature: Double?,
                            pressure: Double?): IHoro {

    val positionMap = points.map { point ->
      point to PositionFunctions.pointPosMap[point]?.getPosition(gmtJulDay, loc, centric, coordinate,
                                                                 starPositionWithAzimuthImpl)
    }.filter { (_, v) -> v != null }
      .map { (point, pos) -> point to pos!! as PositionWithAzimuth }
      .toMap()

    // [0] ~ [12] , 只有 [1] 到 [12] 有值
    val cusps = houseCuspImpl.getHouseCusps(gmtJulDay, loc, houseSystem, coordinate)
    logger.debug("cusps = {}", cusps)

    val cuspDegreeMap = (1 until cusps.size).map {
      it to cusps[it]
    }.toMap()

    return Horoscope(gmtJulDay, loc, houseSystem, coordinate, centric, temperature, pressure, positionMap,
                     cuspDegreeMap)
  }
}