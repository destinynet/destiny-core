/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.HoroscopeFeature
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPattern
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.core.astrology.classical.rules.PatternTranslator
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import java.util.*
import javax.cache.Cache
import javax.inject.Named


@Named
class ClassicalFeature(private val horoscopeFeature: HoroscopeFeature,
                       private val classicalPatternContext: ClassicalPatternContext,
                       @Transient
                       private val classicalRulesCache: Cache<GmtCacheKey<*>, Map<*,*>>) : AbstractCachedFeature<HoroscopeConfig, Map<Planet, List<Pair<IPlanetPattern, String>>>>() {

  override val defaultConfig: HoroscopeConfig = HoroscopeConfig()

  override val gmtCache: Cache<GmtCacheKey<HoroscopeConfig>, Map<Planet, List<Pair<IPlanetPattern, String>>>>
    get() = classicalRulesCache as Cache<GmtCacheKey<HoroscopeConfig>, Map<Planet, List<Pair<IPlanetPattern, String>>>>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: HoroscopeConfig): Map<Planet, List<Pair<IPlanetPattern, String>>> {

    val h = horoscopeFeature.getModel(gmtJulDay, loc, config)

    val factories: List<IPlanetPatternFactory> = classicalPatternContext.let {
      it.essentialDignities.plus(it.accidentalDignities).plus(it.debilities)
    }

    val locale = Locale.TAIWAN

    return Planet.classicalList.associateWith { planet ->
      val list = factories.flatMap { factory ->
        factory.getPatterns(planet, h)
      }.map { pattern ->
        pattern to PatternTranslator.getDescriptor(pattern)
      }.map { (pattern, descriptor) ->
        pattern to descriptor.getDescription(locale)
      }

      list
    }
  }

  companion object {
    const val CACHE_CLASSICAL_RULES = "classicalRulesCache"
  }
}
