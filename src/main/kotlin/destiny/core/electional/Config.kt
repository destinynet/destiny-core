/**
 * Created by smallufo on 2025-06-06.
 */
package destiny.core.electional

import destiny.core.astrology.AstrologyTraversalConfig
import destiny.core.chinese.eightwords.EwTraversalConfig


data class Config(
  val ewConfig: EwTraversalConfig? = EwTraversalConfig(),
  val astrologyConfig: AstrologyTraversalConfig? = AstrologyTraversalConfig(),
)
