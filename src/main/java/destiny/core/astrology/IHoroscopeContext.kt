/**
 * Created by smallufo on 2018-05-07.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.IVoidCourse
import destiny.core.astrology.classical.rules.Misc
import destiny.core.calendar.GmtJulDay
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
interface IHoroscopeContext : Serializable {

  val pointPosFuncMap: Map<Point, IPosition<*>>
  val houseSystem: HouseSystem
  val coordinate: Coordinate
  val centric: Centric
  val temperature: Double
  val pressure: Double
  val voidCourseImpl: IVoidCourse


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  fun getHoroscope(gmtJulDay: GmtJulDay,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point>): IHoroscopeModel


  /** 承上 , [ChronoLocalDateTime] 版本 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>,
                   loc: ILocation,
                   place: String?,
                   points: Collection<Point> = defaultPoints): IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, place, points)
  }

  fun getHoroscope(bdnp: IBirthDataNamePlace , points: Collection<Point> = defaultPoints) : IHoroscopeModel {
    return getHoroscope(bdnp.time , bdnp.location , bdnp.place , points)

  }

  /** 最精簡 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation): IHoroscopeModel {
    return getHoroscope(lmt, loc, null, defaultPoints)
  }



  companion object {

    val defaultPoints = setOf(
      *Planet.array,
      *Axis.array,
      LunarNode.NORTH_MEAN, LunarNode.SOUTH_MEAN)
  }
}

class HoroscopeContext(
  val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  val houseCuspImpl: IHouseCusp,
  override val pointPosFuncMap: Map<Point, IPosition<*>>,
  val points: Collection<Point> = pointPosFuncMap.keys,
  override val voidCourseImpl: IVoidCourse,
  override val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override val coordinate: Coordinate = Coordinate.ECLIPTIC,
  override val centric: Centric = Centric.GEO,
  override val temperature: Double = 0.0,
  override val pressure: Double = 1013.25) : IHoroscopeContext, Serializable {

  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  override fun getHoroscope(gmtJulDay: GmtJulDay,
                            loc: ILocation,
                            place: String?,
                            points: Collection<Point>): IHoroscopeModel {


    val positionMap: Map<Point, IPosWithAzimuth> = points.map { point ->
      point to pointPosFuncMap[point]?.getPosition(gmtJulDay, loc, centric, coordinate, temperature, pressure)
    }.filter { (_, v) -> v != null }.associate { (point, pos) -> point to pos!! as IPosWithAzimuth }


    // [0] ~ [12] , 只有 [1] 到 [12] 有值
    val cusps = houseCuspImpl.getHouseCusps(gmtJulDay, loc, houseSystem, coordinate)
    logger.debug("cusps = {}", cusps)

    val cuspDegreeMap = (1 until cusps.size).associateWith {
      cusps[it]
    }

    // 行星空亡表
    val vocMap: Map<Planet, Misc.VoidCourse> = voidCourseImpl.getVocMap(gmtJulDay, loc, pointPosFuncMap, points)

    return HoroscopeModel(gmtJulDay, loc, place, houseSystem, coordinate, centric, temperature, pressure, positionMap, cuspDegreeMap, vocMap)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is HoroscopeContext) return false

    if (points != other.points) return false
    if (houseSystem != other.houseSystem) return false
    if (coordinate != other.coordinate) return false
    if (centric != other.centric) return false
    if (temperature != other.temperature) return false
    if (pressure != other.pressure) return false

    return true
  }

  override fun hashCode(): Int {
    var result = points.hashCode()
    result = 31 * result + houseSystem.hashCode()
    result = 31 * result + coordinate.hashCode()
    result = 31 * result + centric.hashCode()
    result = 31 * result + temperature.hashCode()
    result = 31 * result + pressure.hashCode()
    return result
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
                         points: Collection<Point> = IHoroscopeContext.defaultPoints): IPersonHoroscopeModel {
    val horoscope = getHoroscope(lmt, loc, place, points)
    return PersonHoroscopeModel(horoscope, gender, name)
  }

  fun getPersonHoroscope(birthData: IBirthDataNamePlace): IPersonHoroscopeModel {
    return getPersonHoroscope(birthData.time, birthData.location, birthData.place, birthData.gender, birthData.name)
  }
}

class PersonHoroscopeContext(private val horoContext: IHoroscopeContext) : IPersonHoroscopeContext, IHoroscopeContext by horoContext
