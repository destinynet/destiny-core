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
 */
interface IHoroscopeContext {

  val houseSystem: HouseSystem
  val coordinate: Coordinate
  val centric: Centric


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  fun getHoroscope(gmtJulDay: Double,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point>? = defaultPoints,
                   houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                   coordinate: Coordinate? = Coordinate.ECLIPTIC,
                   centric: Centric? = Centric.GEO,
                   temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoroscopeModel


  /** 最完整 , [ChronoLocalDateTime] 版本 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point>? = defaultPoints,
                   houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                   coordinate: Coordinate? = Coordinate.ECLIPTIC,
                   centric: Centric? = Centric.GEO,
                   temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, place, points, houseSystem, coordinate, centric, temperature, pressure)
  }


  /** 最精簡 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation): IHoroscopeModel {
    return getHoroscope(lmt, loc, null, defaultPoints, houseSystem, coordinate, centric )
  }

  companion object {
    val defaultPoints = setOf(
      *Planet.array,
      *Rsmi.array,
      //*Hamburger.array,
      //*FixedStar.array,
      LunarNode.NORTH_MEAN,LunarNode.SOUTH_MEAN
      //*LunarNodes.meanArray
    )
  }
}

class HoroscopeContext(
  val points: Collection<Point>,
  override val houseSystem: HouseSystem,
  override val coordinate: Coordinate,
  override val centric: Centric,
  val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  val houseCuspImpl: IHouseCusp ,
  private val pointPosMap: Map<Point, IPosition<*>> ) : IHoroscopeContext, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  override fun getHoroscope(gmtJulDay: Double,
                            loc: ILocation,
                            place: String?,
                            points: Collection<Point>?,
                            houseSystem: HouseSystem?,
                            coordinate: Coordinate?,
                            centric: Centric?,

                            temperature: Double?,
                            pressure: Double?): IHoroscopeModel {

    val finalHs = houseSystem ?: this.houseSystem
    val finalCentric = centric ?: this.centric
    val finalCoordinate = coordinate ?: this.coordinate

    val positionMap: Map<Point, IPosWithAzimuth> = (points ?: this.points).map { point ->
      point to pointPosMap[point]?.getPosition(gmtJulDay, loc, finalCentric, finalCoordinate)
    }.filter { (_, v) -> v != null }
      .map { (point, pos) -> point to pos!! as IPosWithAzimuth }
      .toMap()


    // [0] ~ [12] , 只有 [1] 到 [12] 有值
    val cusps =
      houseCuspImpl.getHouseCusps(gmtJulDay, loc, finalHs, finalCoordinate)
    logger.debug("cusps = {}", cusps)

    val cuspDegreeMap = (1 until cusps.size).map {
      it to cusps[it]
    }.toMap()

    return HoroscopeModel(gmtJulDay, loc, place, finalHs, finalCoordinate,
                          finalCentric, temperature, pressure, positionMap,
                          cuspDegreeMap)
  }


  companion object {
    val defaultPoints = setOf<Point>(
      *Planet.array,
      *Asteroid.array,
      *Hamburger.array,
      *FixedStar.array,
      *LunarNode.meanArray)
  }
}

/**
 * 與「人」相關的星盤資料
 */
interface IPersonHoroscopeContext : IHoroscopeContext {

  fun getPersonHoroscope(lmt: ChronoLocalDateTime<*>,
                         loc: ILocation,
                         place: String?,
                         gender: Gender,
                         name: String?,

                         houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                         coordinate: Coordinate? = Coordinate.ECLIPTIC,
                         centric: Centric? = Centric.GEO,

                         points: Collection<Point>? = IHoroscopeContext.defaultPoints): IPersonHoroscopeModel {
    val horoscope = getHoroscope(lmt, loc, place, points, houseSystem, coordinate, centric, 0.0, 1013.25)
    return PersonHoroscopeModel(horoscope, gender, name)
  }

  fun getPersonHoroscope(birthData: IBirthDataNamePlace): IPersonHoroscopeModel {
    return getPersonHoroscope(birthData.time, birthData.location, birthData.place, birthData.gender, birthData.name)
  }
}

class PersonHoroscopeContext(
  private val hContext: IHoroscopeContext) : IPersonHoroscopeContext, IHoroscopeContext by hContext