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


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  fun getHoroscope(gmtJulDay: Double,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point>? = defaultPoints,
                   houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                   centric: Centric? = Centric.GEO,
                   coordinate: Coordinate? = Coordinate.ECLIPTIC,
                   temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoroscopeModel



  /** 最完整 , [ChronoLocalDateTime] 版本 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>,
                   loc: ILocation,
                   place: String?=null,
                   points: Collection<Point>?,
                   houseSystem: HouseSystem?,
                   centric: Centric?,
                   coordinate: Coordinate?,
                   temperature: Double? = 0.0,
                   pressure: Double? = 1013.25): IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, place, points, houseSystem, centric, coordinate, temperature, pressure)
  }


  /** 最精簡 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation): IHoroscopeModel {
    return getHoroscope(lmt, loc , null , defaultPoints , HouseSystem.PLACIDUS , Centric.GEO , Coordinate.ECLIPTIC)
  }

  companion object {
    val defaultPoints = setOf<Point>(
      *Planets.array,
      *Asteroids.array,
      *Hamburgers.array,
      *FixedStars.array,
      *LunarNodes.meanArray)
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


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  override fun getHoroscope(gmtJulDay: Double,
                            loc: ILocation,
                            place: String?,
                            points: Collection<Point>?,
                            houseSystem: HouseSystem?,
                            centric: Centric?,
                            coordinate: Coordinate?,
                            temperature: Double?,
                            pressure: Double?): IHoroscopeModel {

    println("hs = $houseSystem , centric = $centric , coordinate = $coordinate")
    val positionMap: Map<Point, PositionWithAzimuth> = (points?:this.points).map { point ->
      point to PositionFunctions.pointPosMap[point]?.getPosition(gmtJulDay, loc, centric ?: this.centric,
                                                                 coordinate ?: this.coordinate,
                                                                 starPositionWithAzimuthImpl)
    }.filter { (_, v) -> v != null }
      .map { (point, pos) -> point to pos!! as PositionWithAzimuth }
      .toMap()


    // [0] ~ [12] , 只有 [1] 到 [12] 有值
    val cusps =
      houseCuspImpl.getHouseCusps(gmtJulDay, loc, houseSystem ?: this.houseSystem, coordinate ?: this.coordinate)
    logger.debug("cusps = {}", cusps)

    val cuspDegreeMap = (1 until cusps.size).map {
      it to cusps[it]
    }.toMap()

    return HoroscopeModel(gmtJulDay, loc, place, houseSystem ?: this.houseSystem, coordinate ?: this.coordinate,
                          centric ?: this.centric, temperature, pressure, positionMap,
                          cuspDegreeMap)
  }


  companion object {
    val defaultPoints = setOf<Point>(
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
    val h = hContext.getHoroscope(lmt=lmt, loc=loc)
    return PersonHoroscopeModel(h, gender, name)
  }

}