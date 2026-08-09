/**
 * Created by smallufo on 2026-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.tools.JSerializable
import kotlinx.serialization.Serializable

/** 八字排盤的 view 設定，不影響任何計算結果 */
@Serializable
data class EwViewSettings(

  /** 四柱排列，右至左 or 左至右 */
  val direction: Direction = Direction.R2L,

  /** 是否顯示納音 */
  val showNaYin: Boolean = false

) : JSerializable
