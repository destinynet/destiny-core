/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseFeature
import destiny.core.astrology.classical.rules.Misc
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable


@Serializable
data class HoroscopeConfig(
  val points: Set<@Serializable(with = PointSerializer::class) Point> = setOf(*Planet.array),
  val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  val coordinate: Coordinate = Coordinate.ECLIPTIC,
  val centric: Centric = Centric.GEO,
  val temperature: Double = 0.0,
  val pressure: Double = 1013.25,
  val vocImpl: VoidCourseConfig.VoidCourseImpl = VoidCourseConfig.VoidCourseImpl.Medieval,
  val place: String? = null
)

@DestinyMarker
class HoroscopeConfigBuilder : Builder<HoroscopeConfig> {
  var points: Set<Point> = setOf(*Planet.array)
  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var centric: Centric = Centric.GEO
  var temperature: Double = 0.0
  var pressure: Double = 1013.25
  var vocImpl: VoidCourseConfig.VoidCourseImpl = VoidCourseConfig.VoidCourseImpl.Medieval
  var place: String? = null

  override fun build(): HoroscopeConfig {
    return HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure, vocImpl, place)
  }

  companion object {
    fun horoscope(block : HoroscopeConfigBuilder.() -> Unit = {}) : HoroscopeConfig {
      return HoroscopeConfigBuilder().apply(block).build()
    }
  }
}

class HoroscopeFeature(private val pointPosFuncMap: Map<Point, IPosition<*>> ,
                       private val houseCuspFeature: HouseCuspFeature,
                       private val voidCourseFeature: VoidCourseFeature) : Feature<HoroscopeConfig, IHoroscopeModel> {
  override val key: String = "horoscope"

  override val defaultConfig: HoroscopeConfig = HoroscopeConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HoroscopeConfig): IHoroscopeModel {

    val positionMap: Map<Point, IPosWithAzimuth> = config.points.map { point ->
      point to pointPosFuncMap[point]?.getPosition(gmtJulDay, loc, config.centric, config.coordinate, config.temperature, config.pressure)
    }.filter { (_, v) -> v != null }.associate { (point, pos) -> point to pos!! as IPosWithAzimuth }


    // [1] 到 [12] 宮首黃道度數
    val cuspDegreeMap: Map<Int, ZodiacDegree> = houseCuspFeature.getModel(gmtJulDay, loc, HouseConfig(config.houseSystem, config.coordinate))

    // 行星空亡表
    val vocMap: Map<Planet, Misc.VoidCourse> = voidCourseFeature.getVocMap(gmtJulDay, loc, config.points, VoidCourseConfig(vocImpl = config.vocImpl))

    return HoroscopeModel(gmtJulDay, loc, config, positionMap, cuspDegreeMap, vocMap)
  }
}
