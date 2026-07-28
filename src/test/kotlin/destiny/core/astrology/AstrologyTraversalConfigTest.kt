/**
 * Created by smallufo on 2026-07-28.
 */
package destiny.core.astrology

import destiny.core.astrology.Planet.MARS
import destiny.core.astrology.Planet.MERCURY
import destiny.core.astrology.Planet.SUN
import destiny.core.astrology.Planet.VENUS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AstrologyTraversalConfigTest {

  /**
   * SP peakOrb 預設範圍：SECONDARY 的日、水、金、火。
   * Moon 太快（0.10° 僅 ±2.8 真實天，點事件已足夠）；
   * Jupiter 以下太慢（span 超過整個年度視窗，逐月標記變噪音）。
   * TP/MP 維持點事件；頂層（真實 transit）不受影響。
   */
  @Test
  fun peakOrbDefaults() {
    val config = AstrologyTraversalConfig()

    assertEquals(setOf(SUN, MERCURY, VENUS, MARS), config.secondaryProgressionConfig.peakOrbPlanets)
    assertEquals(0.10, config.secondaryProgressionConfig.peakOrb)

    assertTrue(config.tertiaryProgressionConfig.peakOrbPlanets.isEmpty())
    assertTrue(config.peakOrbPlanets.isEmpty())
  }
}
