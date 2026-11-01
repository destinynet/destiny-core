package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.IClassicalConfig
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable


/**
 * 要判哪些格局（[factories]），以及呼叫端已判定的日夜盤 [sect]（見 [destiny.core.astrology.classical.rules.ClassicalInput.sect]；
 * null ＝ 由 factory 以盤面時刻現算）。兩者都是快取鍵的一部分
 */
@Serializable
data class ClassicalConfig(
  override var factories: List<IPlanetPatternFactory>,
  override val sect: DayNight? = null,
) : IClassicalConfig

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
