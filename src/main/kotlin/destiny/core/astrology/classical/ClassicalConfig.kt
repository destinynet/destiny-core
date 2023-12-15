package destiny.core.astrology.classical

import destiny.core.astrology.IClassicalConfig
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable


@Serializable
data class ClassicalConfig(override var factories: List<IPlanetPatternFactory>) : IClassicalConfig

context(ClassicalPatternContext)
@DestinyMarker
class ClassicalConfigBuilder : Builder<ClassicalConfig> {

  var factories: MutableList<IPlanetPatternFactory> = buildList {
    addAll(essentialDignities)
  }.toMutableList()

  override fun build(): ClassicalConfig {
    return ClassicalConfig(factories)
  }

  companion object {
    context(ClassicalPatternContext)
    fun classical(block: ClassicalConfigBuilder.() -> Unit = {}): ClassicalConfig {
      return ClassicalConfigBuilder().apply(block).build()
    }
  }
}
