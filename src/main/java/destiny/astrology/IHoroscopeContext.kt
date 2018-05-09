/**
 * Created by smallufo on 2018-05-07.
 */
package destiny.astrology

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 與「人」無關的星盤資料
 * 沒有性別 [destiny.core.Gender]
 *
 * to replace [IHoroscope]
 */
interface IHoroscopeContext {

  fun getHoroscope(gmtJulDay: Double, loc: ILocation,
                   place: String?,
                   temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoroscopeModel

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String?, temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, place, temperature, pressure)
  }

  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation): IHoroscopeModel {
    return getHoroscope(lmt, loc, null, null, null)
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
                            place: String?,
                            temperature: Double?,
                            pressure: Double?): IHoroscopeModel {

    val positionMap: Map<Point, PositionWithAzimuth> = points.map { point ->
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

    return HoroscopeModel(gmtJulDay, loc, place, houseSystem, coordinate, centric, temperature, pressure, positionMap,
                          cuspDegreeMap)
  }

  companion object {
    val defaultPoints = setOf(
      *Planets.array,
      *Asteroids.array,
      *Hamburgers.array,
      *FixedStars.array,
      *LunarNodes.meanArray)
  }
}

/**
 * 與「人」相關的星盤資料
 */
interface IPersonHoroscopeContext : IHoroscopeContext {

  fun getHoroscope(lmt: ChronoLocalDateTime<*>,
                   loc: ILocation,
                   place: String?,
                   gender: Gender,
                   name: String?): IPersonHoroscopeModel

  fun getHoroscope(birthData: IBirthDataNamePlace): IPersonHoroscopeModel {
    return getHoroscope(birthData.time, birthData.location, birthData.place, birthData.gender, birthData.name)
  }
}

class PersonHoroscopeContext(private val hContext: HoroscopeContext) : IPersonHoroscopeContext,
  IHoroscopeContext by hContext {
  override fun getHoroscope(lmt: ChronoLocalDateTime<*>,
                            loc: ILocation,
                            place: String?,
                            gender: Gender,
                            name: String?): IPersonHoroscopeModel {
    val h = hContext.getHoroscope(lmt, loc, place)
    return PersonHoroscopeModel(h, gender, name)
  }

}