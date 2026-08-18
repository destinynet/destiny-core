/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.Planet
import destiny.core.astrology.SynastryAspect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

internal class ProgressedAspectTest {

  /**
   * [SynastryAspect] 的 `angle` 是在建構期間由 `aspect.degree` 推導出來的，
   * 且它自訂了 `equals` / `hashCode`。
   *
   * 原本這個測試只是 new 一個物件、不做任何斷言 —— 實際上只驗到「建構子不拋例外」。
   */
  @Test
  fun testInitOk() {
    val aspect = SynastryAspect(
      Planet.MOON, Planet.SUN, 1, 1, Aspect.CONJUNCTION, 0.1,
      IPointAspectPattern.AspectType.APPLYING, null
    )

    assertEquals(Aspect.CONJUNCTION.degree, aspect.angle)
    assertEquals(Planet.MOON, aspect.outerPoint)
    assertEquals(Planet.SUN, aspect.innerPoint)
    assertEquals(1, aspect.outerPointHouse)
    assertEquals(1, aspect.innerPointHouse)
    assertEquals(IPointAspectPattern.AspectType.APPLYING, aspect.aspectType)
    assertNull(aspect.score)

    // 自訂的 equals / hashCode：同內容必須相等且 hash 一致
    val same = SynastryAspect(
      Planet.MOON, Planet.SUN, 1, 1, Aspect.CONJUNCTION, 0.1,
      IPointAspectPattern.AspectType.APPLYING, null
    )
    assertEquals(aspect, same)
    assertEquals(aspect.hashCode(), same.hashCode())

    val other = SynastryAspect(
      Planet.MOON, Planet.SUN, 1, 1, Aspect.OPPOSITION, 0.1,
      IPointAspectPattern.AspectType.APPLYING, null
    )
    assertNotEquals(aspect, other)
    assertEquals(Aspect.OPPOSITION.degree, other.angle)
  }
}
