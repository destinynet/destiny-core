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
    /** 占星盤的設定 */
    val horoscopeConfig : IHoroscopeConfig = HoroscopeConfig(),
    /** 計算星體交角 */
    val aspect: Boolean = true,
    /** 月亮空亡 */
    val voc: Boolean = true,
    /** 星體滯留 */
    val stationary: Boolean = true,
    /** 星體逆行 */
    val retrograde: Boolean = true,
    /** 日食、月食 */
    val eclipse : Boolean = true,
    /** 月相 */
    val lunarPhase: Boolean = true,
    /**
     * 是否在 DTO 中包含「過運星體 to 本命星體」的詳細相位資料。預設為 false
     */
    val includeTransitToNatalAspects: Boolean = false,
    /** 星體換星座 */
    val ingress : Boolean = true
  )
}
