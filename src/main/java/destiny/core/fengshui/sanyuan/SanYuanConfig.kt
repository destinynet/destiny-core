/**
 * Created by smallufo on 2022-02-13.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.Mountain
import destiny.core.iching.Symbol
import kotlinx.serialization.Serializable

/**
 * 運 (1~9)
 */
@JvmInline
@Serializable
value class Period(val value: Int) {
  init {
    require(value >= 1)
    require(value <= 9)
  }
}

@Serializable
data class SanYuanConfig(val period: Period, val mountain: Mountain, val view: Symbol) : java.io.Serializable
