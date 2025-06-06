/**
 * Created by smallufo on 2025-06-06.
 */
package destiny.core.electional

import destiny.core.calendar.eightwords.IPersonPresentConfig
import destiny.core.chinese.eightwords.PersonPresentConfig


data class Config(
  val ewConfig: EwConfig? = EwConfig(),
  val astrologyConfig: AstrologyConfig? = AstrologyConfig(),
) {
  data class EwConfig(
    /** 神煞 */
    val shanSha: Boolean = true,
    val personPresentConfig: IPersonPresentConfig = PersonPresentConfig(),
  )

  data class AstrologyConfig(
    /** 月亮空亡 */
    val voc: Boolean = true,
    /** 星體逆行 */
    val retrograde: Boolean = true,
  )
}
