/**
 * Created by smallufo on 2020-01-04.
 */
package destiny.fengshui

import destiny.core.ITimeLoc
import destiny.core.TimeLoc


data class CompassMapOverlayModel(
  val timeLoc : TimeLoc,
  val place : String? = null,
  val width: Int = 640,
  val rotate: Double = 180.0,
  val zoom: Int = 15,
  // 不透明度 , 0.0 最透明
  val opaque: Float = 0.4f,
  // 放大倍數 1,2 or 4
  val scale: Int = 1
) : ITimeLoc by timeLoc
