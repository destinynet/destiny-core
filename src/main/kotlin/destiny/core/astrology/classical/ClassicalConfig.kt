package destiny.core.astrology.classical

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.IHoroscopeConfig
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable

interface IClassicalConfig : IHoroscopeConfig {
  var factories: List<IPlanetPatternFactory>
  val classicalConfig: ClassicalConfig
    get() = ClassicalConfig(factories)
}

@Serializable
data class ClassicalConfig(override var factories: List<IPlanetPatternFactory>,
                           val horoConfig: IHoroscopeConfig = HoroscopeConfig()) : IClassicalConfig, IHoroscopeConfig by horoConfig

context(ClassicalPatternContext, IHoroscopeConfig)
@DestinyMarker
class ClassicalConfigBuilder : Builder<ClassicalConfig> {

  var factories: MutableList<IPlanetPatternFactory> = buildList {
    addAll(essentialDignities)
  }.toMutableList()

  override fun build(): ClassicalConfig {
    return ClassicalConfig(factories, horoscopeConfig)
  }

  companion object {
    context(ClassicalPatternContext, IHoroscopeConfig)
    fun classical(block: ClassicalConfigBuilder.() -> Unit = {}): ClassicalConfig {
      return ClassicalConfigBuilder().apply(block).build()
    }
  }
}
