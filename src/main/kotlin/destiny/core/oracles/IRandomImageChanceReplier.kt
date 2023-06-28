/**
 * Created by smallufo on 2020-11-17.
 */
package destiny.core.oracles

import destiny.core.Gender


interface IRandomImageChanceReplier<M : IOracleViewModel<*, *>> {

  /** 底圖的寬度比 */
  val photoRatioW: Double
    get() = 2.0

  /** 底圖的高度比 */
  val photoRatioH: Double
    get() = 3.0

  /** 支援哪些寬度的圖檔. 高度則由 [photoRatioW] [photoRatioH] 比值求得 */
  val widths: List<Int>
    get() = listOf(400, 600, 640, 800, 1000)

  /** 支援的底圖解析度 寬/高 */
  val photoSizes: Set<Pair<Int, Int>>
    get() {
      return widths.map { w ->
        w to (w * photoRatioH / photoRatioW).toInt()
      }.toSet()
    }

  /** 隨機求籤 */
  fun getViewModel(question: String?, gender: Gender?): M

}

