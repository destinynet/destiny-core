/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPattern
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import jakarta.inject.Named
import javax.cache.Cache


@Named
class ClassicalFeature(private val horoscopeFeature: HoroscopeFeature,
                       classicalPatternContext: ClassicalPatternContext,
                       @Transient
                       private val classicalRulesCache: Cache<GmtCacheKey<*>, Map<*,*>>) :
  AbstractCachedFeature<Pair<IClassicalConfig, IHoroscopeConfig>, Map<Planet, List<IPlanetPattern>>>() {

  override val defaultConfig = ClassicalConfig(
    factories = classicalPatternContext.let {
      it.essentialDignities.plus(it.accidentalDignities).plus(it.debilities)
    }
  ) to HoroscopeConfig()

  @Suppress("UNCHECKED_CAST")
  override val gmtCache: Cache<GmtCacheKey<Pair<IClassicalConfig, IHoroscopeConfig>>, Map<Planet, List<IPlanetPattern>>>
    get() = classicalRulesCache as Cache<GmtCacheKey<Pair<IClassicalConfig, IHoroscopeConfig>>, Map<Planet, List<IPlanetPattern>>>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: Pair<IClassicalConfig, IHoroscopeConfig>): Map<Planet, List<IPlanetPattern>> {

    val (classicalConfig, horoscopeConfig) = config

    val h = horoscopeFeature.getModel(gmtJulDay, loc, horoscopeConfig)

    return Planet.classicalList.associateWith { planet ->
      classicalConfig.factories.flatMap { factory ->
        factory.getPatterns(planet, h)
      }
    }
  }

  companion object {
    const val CACHE_CLASSICAL_RULES = "classicalRulesCache"
  }
}
