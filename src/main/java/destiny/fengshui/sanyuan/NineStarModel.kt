/**
 * Created by smallufo on 2019-11-25.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import java.io.Serializable

enum class Scale {
  YEAR,
  MONTH,
  DAY,
  HOUR
}

data class NineStarModel(
  val scale: Scale,
  val center: NineStar,
  val map: Map<Symbol, NineStar>) : Serializable
