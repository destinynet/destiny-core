/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc.VoidCourse
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.Feature
import kotlinx.serialization.Serializable


@Serializable
data class VoidCourseConfig(@Serializable(with = PointSerializer::class)
                            val planet: Planet = Planet.MOON,
                            val centric: Centric = Centric.GEO,
                            val impl: VoidCourseImpl = VoidCourseImpl.Medieval) {
  enum class VoidCourseImpl {
    Hellenistic,
    Medieval,
    WilliamLilly
  }
}

class VoidCourseConfigBuilder : Builder<VoidCourseConfig> {
  var planet: Planet = Planet.MOON
  var centric: Centric = Centric.GEO
  var impl: VoidCourseConfig.VoidCourseImpl = VoidCourseConfig.VoidCourseImpl.Medieval

  override fun build(): VoidCourseConfig {
    return VoidCourseConfig(planet, centric, impl)
  }

  companion object {
    fun voidCourse(block : VoidCourseConfigBuilder.() -> Unit = {}) : VoidCourseConfig {
      return VoidCourseConfigBuilder().apply(block).build()
    }
  }

}

class VoidCourseFeature(private val besiegedImpl: IBesieged,
                        private val starPositionImpl: IStarPosition<*>,
                        private val starTransitImpl: IStarTransit,
                        private val pointPosFuncMap: Map<Point, IPosition<*>>) : Feature<VoidCourseConfig , VoidCourse?>{
  override val key: String = "voidCourse"

  override val defaultConfig: VoidCourseConfig = VoidCourseConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: VoidCourseConfig): VoidCourse? {
    val voidCourseImpl = when(config.impl) {
      VoidCourseConfig.VoidCourseImpl.Hellenistic -> VoidCourseHellenistic(besiegedImpl, starPositionImpl)
      VoidCourseConfig.VoidCourseImpl.Medieval -> VoidCourseMedieval(besiegedImpl, starPositionImpl, starTransitImpl)
      VoidCourseConfig.VoidCourseImpl.WilliamLilly -> VoidCourseWilliamLilly(besiegedImpl, starPositionImpl, starTransitImpl)
    }
    return voidCourseImpl.getVoidCourse(gmtJulDay , loc, pointPosFuncMap , config.planet , config.centric)
  }
}
