/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology.classical

import destiny.tools.KvCache
import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.ClassicalInput
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPattern
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature


class ClassicalFeature(private val horoscopeFeature: HoroscopeFeature,
                       classicalPatternContext: ClassicalPatternContext,
                       @Transient
                       private val classicalRulesCache: KvCache<GmtCacheKey<*>, Map<*,*>>) :
  AbstractCachedFeature<Pair<IClassicalConfig, IHoroscopeConfig>, Map<Planet, List<IPlanetPattern>>>() {

  override val defaultConfig = ClassicalConfig(
    factories = classicalPatternContext.let {
      it.essentialDignities.plus(it.accidentalDignities).plus(it.debilities)
    }
  ) to HoroscopeConfig()

  @Suppress("UNCHECKED_CAST")
  override val gmtCache: KvCache<GmtCacheKey<Pair<IClassicalConfig, IHoroscopeConfig>>, Map<Planet, List<IPlanetPattern>>>
    get() = classicalRulesCache as KvCache<GmtCacheKey<Pair<IClassicalConfig, IHoroscopeConfig>>, Map<Planet, List<IPlanetPattern>>>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: Pair<IClassicalConfig, IHoroscopeConfig>): Map<Planet, List<IPlanetPattern>> {

    val (classicalConfig, horoscopeConfig) = config

    val h = horoscopeFeature.getModel(gmtJulDay, loc, horoscopeConfig)
    // 一張盤一份輸入：呼叫端判定的日夜盤、以及各 factory 共用的入相位引擎結果
    val input = ClassicalInput(h, classicalConfig.sect)

    return Planet.classicalList.associateWith { planet ->
      classicalConfig.factories.flatMap { factory ->
        factory.getPatterns(planet, input)
      }
    }
  }

  companion object {
    const val CACHE_CLASSICAL_RULES = "classicalRulesCache"
  }
}
