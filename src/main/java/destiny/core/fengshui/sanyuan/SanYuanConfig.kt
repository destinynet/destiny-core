/**
 * Created by smallufo on 2022-02-13.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.Mountain
import destiny.core.iching.Symbol
import kotlinx.serialization.Serializable

@Serializable
data class SanYuanConfig(val period: Period, val mountain: Mountain, val view: Symbol) : java.io.Serializable
