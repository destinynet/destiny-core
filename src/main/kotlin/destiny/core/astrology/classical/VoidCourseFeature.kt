/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc.VoidCourseSpan
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.Feature
import destiny.tools.serializers.astrology.PlanetSerializer
import jakarta.inject.Named
import kotlinx.serialization.Serializable

interface IVoidCourseConfig : java.io.Serializable {
  var planet: Planet
  var centric: Centric
  var vocImpl: VoidCourseImpl

  val vocConfig : VoidCourseConfig
    get() = VoidCourseConfig(planet, centric, vocImpl)
}

enum class VoidCourseImpl {
  Hellenistic,
  Medieval,
  WilliamLilly
}

@Serializable
data class VoidCourseConfig(@Serializable(with = PlanetSerializer::class)
                            override var planet: Planet = Planet.MOON,
                            override var centric: Centric = Centric.GEO,
                            override var vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval): IVoidCourseConfig

class VoidCourseConfigBuilder : Builder<VoidCourseConfig> {
  var planet: Planet = Planet.MOON
  var centric: Centric = Centric.GEO
  var impl: VoidCourseImpl = VoidCourseImpl.Medieval

  override fun build(): VoidCourseConfig {
    return VoidCourseConfig(planet, centric, impl)
  }

  companion object {
    fun voidCourse(block : VoidCourseConfigBuilder.() -> Unit = {}) : VoidCourseConfig {
      return VoidCourseConfigBuilder().apply(block).build()
    }
  }

}

/**
 * 星體空亡 interface
 */
interface IVoidCourseFeature : Feature<VoidCourseConfig , VoidCourseSpan?> {

  fun getVocMap(gmtJulDay: GmtJulDay, loc: ILocation , points: Set<AstroPoint> , config: VoidCourseConfig): Map<Planet, VoidCourseSpan> {
    return points.filterIsInstance<Planet>()
      .map { planet ->

        planet to getModel(gmtJulDay, loc, config.copy(planet = planet))
      }
      .filter { (_, voc) -> voc != null }
      .associate { (planet, voc) -> planet to voc!! }
  }

  fun getVoidCourses(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, relativeTransitImpl: IRelativeTransit, config: VoidCourseConfig): List<VoidCourseSpan>
}

/**
 * 星體空亡
 */
@Named
class VoidCourseFeature(private val vocMap: Map<VoidCourseImpl, IVoidCourse>,
                        private val pointPosFuncMap: Map<AstroPoint, IPosition<*>>) : IVoidCourseFeature, AbstractCachedFeature<VoidCourseConfig , VoidCourseSpan?>() {
  
  override val key: String = "voidCourse"

  override val defaultConfig: VoidCourseConfig = VoidCourseConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: VoidCourseConfig): VoidCourseSpan? {
    return vocMap[config.vocImpl]?.getVoidCourse(gmtJulDay, loc, pointPosFuncMap, config.planet, config.centric)
  }

  override fun getVoidCourses(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, relativeTransitImpl: IRelativeTransit, config: VoidCourseConfig): List<VoidCourseSpan> {
    return vocMap[config.vocImpl]?.getVoidCourses(fromGmt, toGmt, loc, pointPosFuncMap, relativeTransitImpl, planet = config.planet)?: emptyList()
  }

}
