/**
 * Created by smallufo on 2018-05-07.
 */
package destiny.astrology

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
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
  val temperature: Double
  val pressure: Double


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  fun getHoroscope(gmtJulDay: Double,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point>? = null): IHoroscopeModel


  /** 最完整 , [ChronoLocalDateTime] 版本 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point>? = null): IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, place, points)
  }


  /** 最精簡 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation): IHoroscopeModel {
    return getHoroscope(lmt, loc, null, null)
  }

  companion object {

    val defaultPoints = setOf(
      *Planet.array,
      *Axis.array,
      LunarNode.NORTH_MEAN, LunarNode.SOUTH_MEAN
                             )
  }
}

class HoroscopeContext(
  val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  val houseCuspImpl: IHouseCusp,
  private val pointPosFuncMap: Map<Point, IPosition<*>>,
  val points: Collection<Point> = pointPosFuncMap.keys,
  override val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override val coordinate: Coordinate = Coordinate.ECLIPTIC,
  override val centric: Centric = Centric.GEO,
  override val temperature: Double = 0.0,
  override val pressure: Double = 1013.25) : IHoroscopeContext, Serializable {

  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  override fun getHoroscope(gmtJulDay: Double,
                            loc: ILocation,
                            place: String?,
                            points: Collection<Point>?): IHoroscopeModel {


    val positionMap: Map<Point, IPosWithAzimuth> = (points ?: this.points).map { point ->
      point to pointPosFuncMap[point]?.getPosition(gmtJulDay, loc, centric, coordinate, temperature, pressure)
    }.filter { (_, v) -> v != null }
      .map { (point, pos) -> point to pos!! as IPosWithAzimuth }
      .toMap()


    // [0] ~ [12] , 只有 [1] 到 [12] 有值
    val cusps = houseCuspImpl.getHouseCusps(gmtJulDay, loc, houseSystem, coordinate)
    logger.debug("cusps = {}", cusps)

    val cuspDegreeMap = (1 until cusps.size).map {
      it to cusps[it]
    }.toMap()

    return HoroscopeModel(gmtJulDay, loc, place, houseSystem, coordinate,
                          centric, temperature, pressure, positionMap,
                          cuspDegreeMap)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
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
                         points: Collection<Point>? = IHoroscopeContext.defaultPoints): IPersonHoroscopeModel {
    val horoscope = getHoroscope(lmt, loc, place, points)
    return PersonHoroscopeModel(horoscope, gender, name)
  }

  fun getPersonHoroscope(birthData: IBirthDataNamePlace): IPersonHoroscopeModel {
    return getPersonHoroscope(birthData.time, birthData.location, birthData.place, birthData.gender, birthData.name)
  }
}

class PersonHoroscopeContext(private val horoContext: IHoroscopeContext) : IPersonHoroscopeContext,
  IHoroscopeContext by horoContext
