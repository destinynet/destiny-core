/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import destiny.core.iching.Symbol
import destiny.tools.CircleTools.normalize

/**
 * 整體旋轉 [offsetDeg] 度的 24 山盤:磁北對齊用 —— 磁北盤 = 原盤旋轉「磁偏角」
 * (東偏為正,WMM [destiny.core.IMag] 慣例)。卦位歸屬照 delegate。
 */
class RotatedMountainCompass(
  private val delegate: AbstractMountainCompass,
  private val offsetDeg: Double,
) : AbstractMountainCompass() {

  override val initDegree: Double
    get() = (delegate.initDegree + offsetDeg).normalize()

  override fun getSymbol(mnt: Mountain): Symbol = delegate.getSymbol(mnt)
}
