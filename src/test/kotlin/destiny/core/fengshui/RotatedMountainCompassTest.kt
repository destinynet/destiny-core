/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import kotlin.test.Test
import kotlin.test.assertEquals

class RotatedMountainCompassTest {

  /** 磁北盤 = 地盤整體旋轉磁偏角:台北磁偏 ≈ −4.5°(西偏)→ 子山起點 352.5 − 4.5 = 348 */
  @Test
  fun `西偏 4point5 度的地盤`() {
    val compass = RotatedMountainCompass(EarthlyCompass(), -4.5)
    assertEquals(348.0, compass.getStartDegree(Mountain.子))
    assertEquals(3.0, compass.getEndDegree(Mountain.子))
    assertEquals(168.0, compass.getStartDegree(Mountain.午))
    // 真方位 350° 落在磁北盤的子山 [348, 3)
    assertEquals(Mountain.子, compass.get(350.0))
    // 真方位 3° 已入癸
    assertEquals(Mountain.癸, compass.get(3.0))
  }

  @Test
  fun `offset 0 與 delegate 完全一致`() {
    val base = HeavenlyCompass()
    val rotated = RotatedMountainCompass(base, 0.0)
    for (m in Mountain.entries) {
      assertEquals(base.getStartDegree(m), rotated.getStartDegree(m))
      assertEquals(base.getSymbol(m), rotated.getSymbol(m))
    }
  }

  @Test
  fun `initDegree 正規化(東偏跨 360)`() {
    val compass = RotatedMountainCompass(EarthlyCompass(), 10.0)  // 352.5 + 10 = 362.5 → 2.5
    assertEquals(2.5, compass.getStartDegree(Mountain.子))
    assertEquals(Mountain.壬, compass.get(1.0))  // 尚未過子山起點 → 前一山
  }
}
