package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.IClassicalConfig
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import kotlinx.serialization.Serializable


/**
 * 要判哪些格局（[factories]），以及呼叫端已判定的日夜盤 [sect]（見 [destiny.core.astrology.classical.rules.ClassicalInput.sect]；
 * null ＝ 由 factory 以盤面時刻現算）。兩者都是快取鍵的一部分
 */
@Serializable
data class ClassicalConfig(
  override val factories: List<IPlanetPatternFactory>,
  override val sect: DayNight? = null,
) : IClassicalConfig
