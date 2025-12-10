package destiny.core.astrology.classical

import destiny.core.astrology.IClassicalConfig
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable


@Serializable
data class ClassicalConfig(override var factories: List<IPlanetPatternFactory>) : IClassicalConfig

@DestinyMarker
class ClassicalConfigBuilder(val ctx: ClassicalPatternContext) : Builder<ClassicalConfig> {

  var factories: MutableList<IPlanetPatternFactory> = buildList {
    addAll(ctx.essentialDignities)
  }.toMutableList()

  override fun build(): ClassicalConfig {
    return ClassicalConfig(factories)
  }

  companion object {
    context(ctx: ClassicalPatternContext)
    fun classical(block: ClassicalConfigBuilder.() -> Unit = {}): ClassicalConfig {
      return ClassicalConfigBuilder(ctx).apply(block).build()
    }
  }
}
