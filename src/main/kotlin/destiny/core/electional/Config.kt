/**
 * Created by smallufo on 2025-06-06.
 */
package destiny.core.electional

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.IHoroscopeConfig
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
    val horoscopeConfig : IHoroscopeConfig = HoroscopeConfig(),
    /** 月亮空亡 */
    val voc: Boolean = true,
    /** 星體逆行 */
    val retrograde: Boolean = true,
    /** 日食、月食 */
    val eclipse : Boolean = true,
    /** 月相 */
    val lunarPhase: Boolean = true,
  )
}
