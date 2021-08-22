/**
 * Created by smallufo on 2018-05-07.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.IVoidCourse
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseFeature
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
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
  fun getHoroscope(gmtJulDay: GmtJulDay, loc: ILocation, place: String?, points: Collection<Point>): IHoroscopeModel

  fun getHoroscope(bdnp: IBirthDataNamePlace , points: Collection<Point> = defaultPoints) : IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(bdnp.time, bdnp.location)
    return getHoroscope(gmtJulDay , bdnp.location , bdnp.place , points)
  }

  /** 最精簡 */
  fun getHoroscope(lmt: ChronoLocalDateTime<*>, loc: ILocation): IHoroscopeModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHoroscope(gmtJulDay, loc, null, defaultPoints)
  }



  companion object {
    val defaultPoints = setOf(*Planet.array, *Axis.array, LunarNode.NORTH_MEAN, LunarNode.SOUTH_MEAN)
  }
}

class HoroscopeContext(val houseCuspImpl: IHouseCusp,
                       private val vocMap: Map<VoidCourseConfig.VoidCourseImpl, IVoidCourse>,
                       override val pointPosFuncMap: Map<Point, IPosition<*>>,
                       override val voidCourseImpl: IVoidCourse,
                       private val config: HoroscopeConfig) : IHoroscopeContext, Serializable {

  override val centric: Centric = config.centric
  override val coordinate: Coordinate = config.coordinate
  override val houseSystem: HouseSystem = config.houseSystem
  override val pressure: Double = config.pressure
  override val temperature: Double = config.temperature


  /** 最完整，會覆蓋 [HoroscopeContext] 的參數 */
  override fun getHoroscope(gmtJulDay: GmtJulDay,
                            loc: ILocation,
                            place: String?,
                            points: Collection<Point>): IHoroscopeModel {

    val houseCuspFeature = HouseCuspFeature(houseCuspImpl)
    val voidCourseFeature = VoidCourseFeature(vocMap, pointPosFuncMap)
    val horoscopeFeature = HoroscopeFeature(pointPosFuncMap, houseCuspFeature, voidCourseFeature)
    return horoscopeFeature.getModel(gmtJulDay, loc, config.copy(points = points.toSet()))
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is HoroscopeContext) return false

    if (config != other.config) return false
    return true
  }

  override fun hashCode(): Int {
    return config.hashCode()
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
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val horoscope = getHoroscope(gmtJulDay, loc, place, points)
    return PersonHoroscopeModel(horoscope, gender, name)
  }

  fun getPersonHoroscope(birthData: IBirthDataNamePlace): IPersonHoroscopeModel {
    return getPersonHoroscope(birthData.time, birthData.location, birthData.place, birthData.gender, birthData.name)
  }
}

class PersonHoroscopeContext(private val horoContext: IHoroscopeContext) : IPersonHoroscopeContext, IHoroscopeContext by horoContext
